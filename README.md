# mTLS Java Client

Successfully and statelessly invoke a mTLS endpoint using your Private Key and Certificate from plain String with no environment setup.

This is a code reference for you to copy, not a dependency lib.

## Getting Started

Quickly check this working:

```bash
git clone https://github.com/labbsr0x/mtls-java-client.git
docker-compose up --build
```

This will bring 2 containers up: one with Java8 client and another with Java11. Those containers has the Java code that requests the ready to test mTLS endpoint https://sidecar.mtls.labbs.com.br. The client key and client certificate are provided directly in the code as String variables, to give an example on how to do mTLS without setting any environment properties, VM params or even accessing files from disk. This is useful if you are building a solution that you will use certificates from an input parameter (like an API request).

Those are pure Java8 and Java11 components, so you don't need to download any dependencies to use them.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
