/* 
 *  Copyright (c) 2024 Cisco Systems, Inc.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	"go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp"
	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/baggage"
	"go.opentelemetry.io/otel/exporters/otlp/otlptrace/otlptracegrpc"
	"go.opentelemetry.io/otel/propagation"
	"go.opentelemetry.io/otel/sdk/resource"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
	semconv "go.opentelemetry.io/otel/semconv/v1.7.0"
	"go.opentelemetry.io/otel/trace"
)

const (
	service     = "server"
	application = "MD-HTTP"
)

// Initializes an OTLP exporter, and configures the corresponding trace and
// metric providers.
func initProvider() func() {
	ctx := context.Background()

	appdAppName := os.Getenv("OTEL_SERVICE_NAMESPACE")
	if appdAppName == "" {
		appdAppName = application
	}
	appdTierName := os.Getenv("OTEL_SERVICE_NAME")
	if appdTierName == "" {
		appdTierName = service
	}

	res, err := resource.New(ctx,
		resource.WithAttributes(
			// the service name used to display traces in backends
			semconv.ServiceNameKey.String(appdTierName),
			semconv.ServiceNamespaceKey.String(appdAppName),
			semconv.TelemetrySDKLanguageGo,
		),
	)
	handleErr(err, "failed to create resource")

	// If the OpenTelemetry Collector is running on a local cluster (minikube or
	// microk8s), it should be accessible through the NodePort service at the
	// `localhost:30080` endpoint. Otherwise, replace `localhost` with the
	// endpoint of your cluster. If you run the app inside k8s, then you can
	// probably connect directly to the service through dns
	otlpEndpoint := os.Getenv("OTEL_EXPORTER_OTLP_ENDPOINT")
	if otlpEndpoint == "" {
		otlpEndpoint := os.Getenv("OTEL_EXPORTER_OTLP_TRACES_ENDPOINT")
		if otlpEndpoint == "" {
			otlpEndpoint = "localhost:4317"
		}
	}
	conn, err := grpc.DialContext(ctx, otlpEndpoint, grpc.WithTransportCredentials(insecure.NewCredentials()), grpc.WithBlock())
	handleErr(err, "failed to create gRPC connection to collector")

	// Set up a trace exporter
	traceExporter, err := otlptracegrpc.New(ctx, otlptracegrpc.WithGRPCConn(conn))
	handleErr(err, "failed to create trace exporter")

	// Register the trace exporter with a TracerProvider, using a batch
	// span processor to aggregate spans before export.
	bsp := sdktrace.NewBatchSpanProcessor(traceExporter)
	tracerProvider := sdktrace.NewTracerProvider(
		sdktrace.WithSampler(sdktrace.AlwaysSample()),
		sdktrace.WithResource(res),
		sdktrace.WithSpanProcessor(bsp),
	)
	otel.SetTracerProvider(tracerProvider)

	// set global propagator to tracecontext (the default is no-op).
	// otel.SetTextMapPropagator(propagation.TraceContext{})
	// set context propagator baggage
	otel.SetTextMapPropagator(propagation.NewCompositeTextMapPropagator(propagation.TraceContext{}, propagation.Baggage{}))

	return func() {
		// Shutdown will flush any remaining spans and shut down the exporter.
		handleErr(tracerProvider.Shutdown(ctx), "failed to shutdown TracerProvider")
	}
}

func handleErr(err error, message string) {
	if err != nil {
		log.Fatalf("%s: %v", message, err)
	}
}

func hello(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(w, "hello\n")
}

func headers(w http.ResponseWriter, req *http.Request) {
	for name, headers := range req.Header {
		for _, h := range headers {
			fmt.Fprintf(w, "%v: %v\n", name, h)
		}
	}
}

func forward(w http.ResponseWriter, req *http.Request) {
	client := http.Client{}

	ctx := req.Context()
	bag := baggage.FromContext(ctx)
	bag.SetMember(bag.Member("username=donuts"))

	url := req.URL.Query().Get("url")

	var body []byte

	err := func(ctx context.Context) error {

		ctx, span := tracer.Start(
			ctx,
			"call downstream",
			trace.WithSpanKind(trace.SpanKindClient),
			trace.WithAttributes(semconv.PeerServiceKey.String("ExampleService")))
		defer span.End()

		traceparentValue := fmt.Sprintf("00-%s-%s-%s", span.SpanContext().TraceID().String(), span.SpanContext().SpanID().String(), span.SpanContext().TraceFlags().String())
		fmt.Printf("Traceparent: %s\n", traceparentValue)

		// req, _ := http.NewRequestWithContext(ctx, "GET", *url, nil)
		req, _ := http.NewRequest("GET", url, nil)
		req.Header.Add("Traceparent", traceparentValue)

		fmt.Printf("Sending request...\n%v\n", req)
		res, err := client.Do(req)
		if err != nil {
			panic(err)
		}
		body, err = io.ReadAll(res.Body)
		_ = res.Body.Close()

		return err
	}(ctx)
	if err != nil {
		fmt.Fprintf(w, "error getting response from: %s\n", url)
	}

	fmt.Fprintf(w, "response to Go server: %s\n", string(body))
}

func vendor(w http.ResponseWriter, req *http.Request) {
	client := http.Client{}

	ctx := req.Context()
	bag := baggage.FromContext(ctx)
	bag.SetMember(bag.Member("vendorKind=tier_one"))

	vendorsHost := os.Getenv("VENDORS_HOST_PORT_PROTO")
	url := vendorsHost + req.URL.Path

	var body []byte

	err := func(ctx context.Context) error {

		ctx, span := tracer.Start(
			ctx,
			"call to vendors",
			trace.WithSpanKind(trace.SpanKindClient),
			trace.WithAttributes(semconv.PeerServiceKey.String("Vendors")))
		defer span.End()

		traceparentValue := fmt.Sprintf("00-%s-%s-%s", span.SpanContext().TraceID().String(), span.SpanContext().SpanID().String(), span.SpanContext().TraceFlags().String())
		fmt.Printf("Traceparent: %s\n", traceparentValue)

		// req, _ := http.NewRequestWithContext(ctx, "GET", *url, nil)
		req, _ := http.NewRequest("GET", url, nil)
		req.Header.Add("Traceparent", traceparentValue)

		fmt.Printf("Sending request...\n%v\n", req)
		res, err := client.Do(req)
		if err != nil {
			panic(err)
		}
		body, err = io.ReadAll(res.Body)
		_ = res.Body.Close()

		return err
	}(ctx)
	if err != nil {
		fmt.Fprintf(w, "error getting response from: %s\n", url)
	}

	fmt.Fprintf(w, "response to Go server: %s\n", string(body))
}

var tracer trace.Tracer

func main() {

	log.Printf("Waiting for connection...")
	shutdown := initProvider()
	defer shutdown()
	log.Printf("Connected to provider...")

	tracer = otel.Tracer("test-tracer")

	handler := http.NewServeMux()
	fmt.Printf("Tracer: %v", tracer)

	handler.HandleFunc("/go/headers-plain", headers)
	handler.HandleFunc("/go/hello", otelhttp.NewHandler(http.HandlerFunc(hello), "Hello").ServeHTTP)
	handler.HandleFunc("/go/headers", otelhttp.NewHandler(http.HandlerFunc(headers), "Headers").ServeHTTP)
	handler.HandleFunc("/go/fwd", otelhttp.NewHandler(http.HandlerFunc(forward), "Forward").ServeHTTP)
	handler.HandleFunc("/go/forward", otelhttp.NewHandler(http.HandlerFunc(forward), "Forward").ServeHTTP)
	handler.HandleFunc("/api/vendor/", otelhttp.NewHandler(http.HandlerFunc(vendor), "Vendor").ServeHTTP)

	http.ListenAndServe(":8090", handler)
}
