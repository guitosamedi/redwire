# Projet Fil Rouge : Gestion des congés et jours feriés


Groupe 1
- Antoine LiGEROT
- Kamella VERGNAUD
- Liting QIU
- Guito SAMEDI

### Installation
il faut d'aord créer une datasource avec les bonnes credentials,
ou changer ces valeurs dans le application.properties 


spring.datasource.url=jdbc:mariadb://localhost:3306/redwire
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

## Lancer l'application
Au démarrage de l'application des employés seront créés automatiquement avec les rôles
Admin, Manager, une combinaison des deux ou aucun.

il faut être autentifié pour toutes les requetes,
sauf pour l'Url de connexion : /sessions

voici un exemple de requete de connexion pour l'employe qui a pour roles admin et manager :

POST localhost:8080/sessions
Content-Type: application/json

{
"email" : "exampleetout@gmail.com",
"password": "passwordtout"
}

cette méthode renvoie un token d'autentification

le mot de passe des employés ajouté en base au démarrage est leur firstName

une fois authentifié, vous êtes autorisé à faire certaines requètes en fonction de vos roles.
(voir config/WebSecurityConfig pour le détail)


# routes
voir requeest.http pour des exemples