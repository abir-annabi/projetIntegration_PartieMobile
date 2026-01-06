# 🔍 Diagnostic Endpoint Login - Erreur 500

## 🚨 Problème Identifié

**Erreur:** `500 Internal Server Error`
**Message:** `"No static resource api/auth/login"`
**Cause:** Le backend ne reconnaît pas l'endpoint `/api/auth/login`

## 🎯 Endpoints Possibles à Tester

Le backend utilise probablement un de ces endpoints :

### 1. Endpoints Spring Boot Classiques
- `/api/auth/authenticate`
- `/api/auth/signin` 
- `/api/auth/connexion`
- `/api/auth/authentification`
- `/auth/login`
- `/login`

### 2. Endpoints Français
- `/api/auth/connexion`
- `/api/auth/authentification`
- `/api/utilisateur/connexion`

### 3. Endpoints Sans Préfixe API
- `/auth/login`
- `/authenticate`
- `/signin`

## 🔧 Solution Temporaire

Je vais créer une version de test qui essaie plusieurs endpoints automatiquement.

## 📋 Actions à Faire

### Côté Backend (Vérifier)
1. Ouvrir le contrôleur d'authentification
2. Vérifier l'annotation `@PostMapping` 
3. Confirmer l'URL exacte

### Côté Frontend (Test Automatique)
1. Tester plusieurs endpoints
2. Identifier celui qui fonctionne
3. Mettre à jour la configuration

## 🎯 Endpoints les Plus Probables

Basé sur l'erreur, le backend semble chercher une ressource statique, ce qui suggère :
- `/api/auth/authenticate` (le plus probable)
- `/api/auth/signin`
- `/authenticate`