# SQLI-Matchmaking-Backend
Ce projet est une plateforme permettant aux utilisateurs de s'inscrire à des événements sportifs, de créer des équipes, et de gérer leur participation.

## Introduction
Le projet vise à offrir une plateforme web et mobile responsive permettant :

- L'inscription et la connexion des utilisateurs avec la gestion des rôles.
- La création d'événements sportifs (ex: football à 5, badminton, etc.).
- L'inscription aux événements disponibles.
- La génération automatique ou manuelle des équipes pour les événements.
- Des fonctionnalités avancées telles que la notification pour rappeler les inscriptions aux événements récurrents et un système de matchmaking.


## Fonctionnalités principales

### Gestion Utilisateur

- Inscription et connexion des utilisateurs avec différents rôles (administrateur, organisateur, participant).
- Authentification sécurisée.

### Gestion des événements

- Création d'événements sportifs avec des détails spécifiques (date, heure, type de sport, lieu, etc.).
- Inscription des utilisateurs aux événements disponibles.

### Génération des équipes

- Possibilité de générer automatiquement ou manuellement les équipes pour les événements sportifs.

### Fonctionnalités avancées

- Notification pour rappeler les inscriptions aux événements récurrents.
- Système de matchmaking pour équilibrer les équipes lors des événements.


## Technologies Utilisées

- Framework back-end: Spring Boot (Java)
- Base de données: MySQL
- Front-end web: Angualar.js

## Prérequis

Assurez-vous d'avoir installé les outils suivants avant de démarrer :
- Java (version 17.0.8.1)
- Maven (version 3.6.3)


## Installation

1. Clonez le dépôt du projet :

   ```bash
   git clone https://github.com/votre-utilisateur/nom-du-projet.git
   ```
2. Naviguez vers le répertoire du projet :
   ```bash
    cd SQLI-Matchmaking-Backend/matchmaking
   ```
3. Installez les dépendances et lancez l'application :
   ```bash
    mvn spring-boot:run
   ```

4. Lancement de la base de donnée en locale :
   - Installation de MySQL
   ```bash
   sudo apt install mysql-server
   sudo systemctl start mysql
   sudo mysql -u root -p
   ```
   - Création de la base de donnée
   ``` bash
   mysql > CREATE DATABASE nom_de_votre_base_de_donnees;
   mysql > USE DATABASE nom_de_votre_base_de_donnees;
   ```
   - Création d'un nouvel utilisateur :
   ``` bash
   mysql > CREATE USER 'nouvel_utilisateur'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
   ```
   - Attribution des autorisations à cet utilisateur :
   ``` bash
   GRANT ALL PRIVILEGES ON ma_base_de_donnees.* TO 'nouvel_utilisateur'@'localhost';
   ```
   - Appliquation des modifications et fermeture de la console MySQL :
   ``` bash
   FLUSH PRIVILEGES;
   exit;
   ```


# Auteurs
Mouad BOUMOUR - https://github.com/mboumour
Achraf JDIDI - https://github.com/MiniatureRose
Salim BEKKARI - https://github.com/salim085
Anas NAAMI - Liens vers le profil GitHub
Hicham NEKT - Liens vers le profil GitHub
Oussama Zobid - https://github.com/ozombid