## Budowanie projektu

Aby zbudować plik wykonywalny JAR, wpisz w terminalu:

```bash
mvn clean package
```

## Uruchomienie

```bash
java -jar target/AiSD2025ZEx5.jar -m <comp|decomp> -s <plik_wejściowy> -d <plik_wyjściowy> [-l <rozmiar_bloku>]
```

## Przykładowe uruchomienie:

```bash
java -jar target/AiSD2025ZEx5.jar -m comp -s test_big.txt -d dane.comp -l 1
java -jar target/AiSD2025ZEx5.jar -m decomp -s dane.comp -d dane_odzyskane.txt
```