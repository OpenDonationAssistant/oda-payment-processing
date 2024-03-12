FROM fedora:39
WORKDIR /app
COPY target/oda-payment-processing /app

CMD ["./oda-payment-processing"]

