FROM openjdk:8

WORKDIR /ImgurServiceApp/target/universal

COPY . /ImgurServiceApp

EXPOSE 9000

CMD ./imgurserviceapp-1.0/bin/imgurserviceapp -Dplay.http.secret.key=test
