# Build the manager binary
FROM harbor.middleware.com/build/golang:1.14.15-stretch AS builder

WORKDIR /workspace
COPY .  .
# Build
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 GO111MODULE=on go build -mod=vendor -a -o main cmd/change-request/main.go

#FROM harbor.middleware.com/middleware/busybox:1.33.1
FROM busybox
ARG TARGETARCH
WORKDIR /
COPY --from=builder /workspace/main .


ENTRYPOINT ["/main"]
