# 🔍 Investigation: Pourquoi "Mes Programmes" n'affiche que 2 programmes

## 🎯 Problème
L'utilisateur rapporte que la page "Mes Programmes" n'affiche que 2 programmes au lieu de tous les programmes assignés à l'utilisateur.

## 🔧 Modifications de Debug Ajoutées

### 1. **MesProgrammesViewModel.kt**
- ✅ Logging détaillé de chaque programme reçu du backend
- ✅ Affichage des détails: ID, nom, description, durée, statut, utilisateur
- ✅ Comptage des plats et activités par programme
- ✅ Alerte spéciale si moins de 3 programmes sont retournés

### 2. **ProgrammeRepository.kt**
- ✅ Logging de l'appel API `/api/programmes/historique`
- ✅ Diagnostic détaillé de la réponse API
- ✅ Analyse des erreurs HTTP (404, 403, 500, timeout, connexion)
- ✅ Alertes si moins de 3 programmes sont retournés

### 3. **MesProgrammesActivity.kt**
- ✅ Vérification du token d'authentification au démarrage
- ✅ Logging détaillé de l'observer des programmes
- ✅ Bouton de debug (long press sur l'écran pour forcer le rechargement)
- ✅ Affichage des détails des programmes reçus

## 📱 Comment Tester

### Étape 1: Lancer l'application
1. Ouvrez l'application
2. Connectez-vous avec vos identifiants
3. Allez dans "Mes Programmes"

### Étape 2: Analyser les logs
Recherchez dans les logs Android Studio les messages suivants:

```
🔄 DÉBUT du chargement des programmes
✅ Token présent: [token...]
🔄 Appel API: /api/programmes/historique
✅ Réponse API reçue: X programmes
=== PROGRAMME 1/X ===
```

### Étape 3: Forcer le rechargement
- **Long press** sur l'écran "Mes Programmes" pour forcer un rechargement
- Observez les nouveaux logs générés

## 🔍 Points à Vérifier

### 1. **Authentification**
```
✅ Token présent: [token...]
- Utilisateur ID: [id]
- Email: [email]
```
❌ Si "AUCUN TOKEN!", l'utilisateur n'est pas connecté correctement.

### 2. **Réponse API**
```
✅ Réponse API reçue: X programmes
```
- Si X = 2, le backend retourne effectivement seulement 2 programmes
- Si X > 2, le problème est dans l'affichage frontend

### 3. **Contenu des Programmes**
```
Programme 1: [nom] (ID: [id])
  Statut: [statut]
  Plats: [nombre]
  Activités: [nombre]
```

### 4. **Erreurs Possibles**
- **404**: Endpoint non trouvé ou utilisateur sans programmes
- **403**: Problème d'authentification JWT
- **500**: Erreur serveur backend
- **Timeout**: Serveur inaccessible

## 🚨 Causes Probables

### 1. **Backend ne retourne que 2 programmes**
- L'utilisateur n'a réellement que 2 programmes assignés
- Problème de filtrage côté backend
- Données manquantes en base de données

### 2. **Problème d'authentification**
- Token JWT expiré ou invalide
- Mauvais utilisateur identifié côté backend

### 3. **Problème de pagination**
- Le backend limite les résultats (pagination)
- Paramètres de requête manquants

### 4. **Problème de statut**
- Le backend filtre par statut (ex: seulement "EN_COURS")
- Programmes "TERMINÉ" ou "PAUSE" exclus

## 🔧 Solutions à Tester

### 1. **Vérifier la base de données backend**
```sql
SELECT up.id, u.adresse_email, p.nom, up.statut, up.date_debut
FROM user_programmes up
JOIN users u ON up.user_id = u.id
JOIN programmes p ON up.programme_id = p.id
WHERE u.adresse_email = '[email_utilisateur]';
```

### 2. **Tester l'endpoint directement**
```bash
curl -H "Authorization: Bearer [token]" \
     http://10.0.2.2:8100/api/programmes/historique
```

### 3. **Vérifier les logs backend**
- Rechercher les logs de l'endpoint `/api/programmes/historique`
- Vérifier l'utilisateur identifié via JWT
- Vérifier la requête SQL générée

## 📊 Résultats Attendus

Après ces modifications, vous devriez voir dans les logs:
1. Le nombre exact de programmes retournés par l'API
2. Les détails de chaque programme
3. L'identification de l'utilisateur connecté
4. Les erreurs éventuelles d'authentification ou de réseau

## 🎯 Prochaines Étapes

1. **Exécuter l'application** avec les nouveaux logs
2. **Analyser les résultats** dans Android Studio
3. **Identifier la cause racine**:
   - Backend retourne seulement 2 programmes → Problème backend
   - Backend retourne plus de 2 programmes → Problème frontend
4. **Appliquer la correction appropriée**

---

**Note**: Ces modifications de debug peuvent être supprimées une fois le problème résolu pour éviter de surcharger les logs en production.