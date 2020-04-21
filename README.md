# resa-API

### Construction du projet via docker-compose 

Aller dans le dossier récupéré

```
docker-compose up
```

- Initialisation des services.
- Pour alleger votre machine qui hébergera les docker ainsi que le docker-compose, il y a une seul instance mysql au lieu d'une instance par microservice.

### Pour voir les instances de microservice 

- go to localhost:9002, vous pouvez voir les registres des microservices.

### Se connecter a une interface de donnée comme phpMyAdmin (adminer) 

- go to localhost:3000

- credentials
```
serveur : [host]:3306 (le host est découvrable sur votre terminal ou il y a les docker sous le nom de adminer)
user: root
password: azertyuiop
```

# resa-front

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

#### Utilisateur admin pour faire vos test sur la plateforme
user: admin
pass: testtest
