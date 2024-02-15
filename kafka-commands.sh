#!/bin/bash

# Mostrar un mensaje de ayuda
print_help() {
    echo "Uso: $0 [comando]"
    echo "Comandos disponibles:"
    echo "  create-topic [nombre del topic]: Crear un nuevo topic en Kafka"
    echo "  list-topics: Listar los topics existentes en Kafka"
    echo "  describe-topic [nombre del topic]: Describir un topic específico en Kafka"
    echo "  consumer [nombre del topic]: Inicia una consola para ver los mensajes del topic espeficico en Kafka"
    echo "  producer [nombre del topic]: Inicia una consola para enviar mensajes al topic espeficico en Kafka"
}

# Verificar si se proporciona un comando
if [ $# -eq 0 ]; then
    echo "Error: Se requiere al menos un comando."
    print_help
    exit 1
fi

case $1 in
    create-topic)
        if [ $# -lt 2 ]; then
            echo "Error: Se requiere el nombre del topic para crearlo."
            print_help
            exit 1
        fi
        docker-compose exec kafka /opt/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic "$2"
        ;;
    consumer)
        if [ $# -lt 2 ]; then
            echo "Error: Se requiere el nombre del topic para crearlo."
            print_help
            exit 1
        fi
        docker-compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh --topic "$2" --bootstrap-server localhost:9092
        ;;
    producer)
        if [ $# -lt 2 ]; then
            echo "Error: Se requiere el nombre del topic para crearlo."
            print_help
            exit 1
        fi
        docker-compose exec kafka /opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic "$2"
        ;;
    describe-topic)
        if [ $# -lt 2 ]; then
            echo "Error: Se requiere el nombre del topic para describirlo."
            print_help
            exit 1
        fi
        docker-compose exec kafka /opt/kafka/bin/kafka-topics.sh --describe --zookeeper zookeeper:2181 --topic "$2"
        ;;
    *)
        echo "Error: Comando no reconocido."
        print_help
        exit 1
        ;;
esac

exit 0