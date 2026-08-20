FROM ubuntu:latest
LABEL authors="FilipeMessiasdaSilva"

# Ubuntu não vem com Java instalado por padrão, então instalamos o JDK.
# --no-install-recommends evita pacotes extras desnecessários.
# A limpeza do apt no final reduz o tamanho final da imagem.
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjdk-21-jdk && \
    rm -rf /var/lib/apt/lists/*

# Pasta de trabalho dentro do container
WORKDIR /app

# Copia o código-fonte da pasta local (contexto de build) pro container
COPY ServerTCP.java .

# Compila o .java, gerando o ServerTCP.class
RUN javac ServerTCP.java

# Documenta a porta usada (não abre a porta sozinho, é só informativo)
EXPOSE 5000

# Comando executado quando o container sobe: roda o servidor na porta 5000
ENTRYPOINT ["java", "ServerTCP", "5000"]
