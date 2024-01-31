############################
# STEP 1 build executable binary
############################
FROM golang:alpine AS builder
# Install git.
# Git is required for fetching the dependencies.
RUN apk update && apk add --no-cache git
WORKDIR $GOPATH/src/gargoyle/go-http-server/
COPY . .
# Enable local modules
ENV GO111MODULE="on" 
# Fix golang proxy - default often points to China with broken checksums :-( 
ENV GOPROXY="https://proxy.golang.org"
# Fetch dependencies.
# Using go get.
RUN go get -d -v
# Build the binary.
RUN GOOS=linux GOARCH=amd64 go build -ldflags="-w -s" -o /go/bin/go-http-server

############################
# STEP 3 build a small image
############################
FROM alpine:latest
# Copy our static executable.
COPY --from=builder /go/bin/go-http-server /go/bin/go-http-server

# Expose port 8090
EXPOSE 8090 
# Run the hello binary.
WORKDIR /go/bin/
CMD ["./go-http-server"]