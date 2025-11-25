# 🧪 Plan de Tests Manuels - FitLife

## 📋 Checklist Complète des Tests

### ✅ Tests de Validation des Champs

#### Test 1.1: Email - Format invalide
- **Action**: Entrer "test" dans le champ email
- **Résultat attendu**: Message "Email invalide" en rouge sous le champ
- **Statut**: [ ]

#### Test 1.2: Email - Champ vide
- **Action**: Laisser le champ email vide et cliquer sur "Se connecter"
- **Résultat attendu**: Message "Ce champ est obligatoire"
- **Statut**: [ ]

#### Test 1.3: Email - Format valide
- **Action**: Entrer "test@example.com"
- **Résultat attendu**: Pas de message d'erreur, bordure normale
- **Statut**: [ ]

#### Test 1.4: Téléphone - Format invalide (trop court)
- **Action**: Entrer "0612" dans le champ téléphone
- **Résultat attendu**: Message "Numéro de téléphone invalide (format: 06XXXXXXXX)"
- **Statut**: [ ]

#### Test 1.5: Téléphone - Format invalide (mauvais préfixe)
- **Action**: Entrer "0512345678"
- **Résultat attendu**: Message d'erreur de format
- **Statut**: [ ]

#### Test 1.6: Téléphone - Format valide
- **Action**: Entrer "0612345678" ou "0712345678"
- **Résultat attendu**: Pas de message d'erreur
- **Statut**: [ ]

#### Test 1.7: Mot de passe - Trop court
- **Action**: Entrer "123" dans le mot de passe
- **Résultat attendu**: Message "Le mot de passe doit contenir au moins 6 caractères"
- **Statut**: [ ]

#### Test 1.8: Mot de passe - Valide
- **Action**: Entrer "test123"
- **Résultat attendu**: Pas de message d'erreur
- **Statut**: [ ]

#### Test 1.9: Date de naissance - Sélection
- **Action**: Cliquer sur le champ date
- **Résultat attendu**: DatePicker s'ouvre
- **Statut**: [ ]

#### Test 1.10: Date de naissance - Date future bloquée
- **Action**: Essayer de sélectionner une date future dans le DatePicker
- **Résultat attendu**: Dates futures désactivées
- **Statut**: [ ]

### ✅ Tests d'Inscription

#### Test 2.1: Inscription réussie
- **Données**:
  - Nom: Dupont
  - Prénom: Jean
  - Téléphone: 0612345678
  - Email: jean.dupont@test.com
  - Mot de passe: test123
  - Date: 01/01/1990
- **Résultat attendu**: 
  - Toast "Inscription réussie !"
  - Redirection vers Dashboard
  - Affichage des infos utilisateur
- **Statut**: [ ]

#### Test 2.2: Inscription - Email déjà utilisé
- **Action**: S'inscrire avec un email existant
- **Résultat attendu**: Toast "Cette adresse email est déjà utilisée"
- **Statut**: [ ]

#### Test 2.3: Inscription - Téléphone déjà utilisé
- **Action**: S'inscrire avec un téléphone existant
- **Résultat attendu**: Toast "Ce numéro de téléphone est déjà utilisé"
- **Statut**: [ ]

#### Test 2.4: Inscription - Tous les champs vides
- **Action**: Cliquer sur "S'inscrire" sans remplir les champs
- **Résultat attendu**: Messages d'erreur sur tous les champs obligatoires
- **Statut**: [ ]

#### Test 2.5: Inscription - Loading state
- **Action**: Cliquer sur "S'inscrire" avec des données valides
- **Résultat attendu**: 
  - ProgressBar visible
  - Bouton désactivé
  - Texte du bouton disparaît
- **Statut**: [ ]

### ✅ Tests de Connexion

#### Test 3.1: Connexion réussie
- **Données**:
  - Email: jean.dupont@test.com
  - Mot de passe: test123
- **Résultat attendu**:
  - Toast "Connexion réussie !"
  - Redirection vers Dashboard
- **Statut**: [ ]

#### Test 3.2: Connexion - Email incorrect
- **Données**:
  - Email: wrong@test.com
  - Mot de passe: test123
- **Résultat attendu**: Toast "Email ou mot de passe incorrect"
- **Statut**: [ ]

#### Test 3.3: Connexion - Mot de passe incorrect
- **Données**:
  - Email: jean.dupont@test.com
  - Mot de passe: wrongpassword
- **Résultat attendu**: Toast "Email ou mot de passe incorrect"
- **Statut**: [ ]

#### Test 3.4: Connexion - Champs vides
- **Action**: Cliquer sur "Se connecter" sans remplir
- **Résultat attendu**: Messages d'erreur sur les champs
- **Statut**: [ ]

### ✅ Tests de Navigation

#### Test 4.1: Login → Signup
- **Action**: Cliquer sur "S'inscrire" depuis l'écran de login
- **Résultat attendu**: Navigation vers l'écran d'inscription
- **Statut**: [ ]

#### Test 4.2: Signup → Login
- **Action**: Cliquer sur "Se connecter" depuis l'écran d'inscription
- **Résultat attendu**: Retour à l'écran de login
- **Statut**: [ ]

#### Test 4.3: Login → Dashboard
- **Action**: Se connecter avec succès
- **Résultat attendu**: Navigation vers Dashboard, impossible de revenir en arrière
- **Statut**: [ ]

#### Test 4.4: Dashboard → Login (Déconnexion)
- **Action**: Cliquer sur "Déconnexion"
- **Résultat attendu**: Retour à l'écran de login
- **Statut**: [ ]

### ✅ Tests de Persistance

#### Test 5.1: Token persistant
- **Action**: 
  1. Se connecter
  2. Fermer l'application (swipe)
  3. Rouvrir l'application
- **Résultat attendu**: Utilisateur toujours connecté, Dashboard affiché
- **Statut**: [ ]

#### Test 5.2: Déconnexion efface le token
- **Action**:
  1. Se connecter
  2. Se déconnecter
  3. Fermer et rouvrir l'app
- **Résultat attendu**: Écran de login affiché
- **Statut**: [ ]

### ✅ Tests d'Interface

#### Test 6.1: Scroll sur écran d'inscription
- **Action**: Faire défiler l'écran d'inscription
- **Résultat attendu**: Tous les champs accessibles, pas de coupure
- **Statut**: [ ]

#### Test 6.2: Clavier ne cache pas les champs
- **Action**: Cliquer sur le dernier champ (date)
- **Résultat attendu**: Le champ reste visible au-dessus du clavier
- **Statut**: [ ]

#### Test 6.3: Toggle mot de passe
- **Action**: Cliquer sur l'icône œil dans le champ mot de passe
- **Résultat attendu**: Mot de passe visible/masqué
- **Statut**: [ ]

#### Test 6.4: Couleurs du thème
- **Vérification**:
  - Fond noir (#1A1A1A)
  - Boutons orange avec gradient
  - Texte blanc pour le contenu principal
  - Texte gris pour le secondaire
  - Erreurs en rouge
- **Statut**: [ ]

### ✅ Tests de Réseau

#### Test 7.1: Backend inaccessible
- **Action**: Arrêter le backend et essayer de se connecter
- **Résultat attendu**: Toast "Erreur réseau. Vérifiez votre connexion"
- **Statut**: [ ]

#### Test 7.2: Timeout
- **Action**: Simuler une connexion lente
- **Résultat attendu**: Loading state puis message d'erreur après timeout
- **Statut**: [ ]

#### Test 7.3: Réponse 500 du serveur
- **Action**: Provoquer une erreur serveur
- **Résultat attendu**: Toast avec message d'erreur approprié
- **Statut**: [ ]

### ✅ Tests de Rotation d'Écran

#### Test 8.1: Rotation sur login
- **Action**: Remplir les champs puis tourner l'écran
- **Résultat attendu**: Données conservées dans les champs
- **Statut**: [ ]

#### Test 8.2: Rotation pendant loading
- **Action**: Cliquer sur "Se connecter" puis tourner l'écran
- **Résultat attendu**: Loading state conservé, pas de crash
- **Statut**: [ ]

### ✅ Tests de Performance

#### Test 9.1: Temps de réponse login
- **Action**: Se connecter et mesurer le temps
- **Résultat attendu**: < 2 secondes (réseau local)
- **Statut**: [ ]

#### Test 9.2: Temps de réponse signup
- **Action**: S'inscrire et mesurer le temps
- **Résultat attendu**: < 2 secondes (réseau local)
- **Statut**: [ ]

#### Test 9.3: Fluidité de l'UI
- **Action**: Naviguer entre les écrans
- **Résultat attendu**: Transitions fluides, pas de lag
- **Statut**: [ ]

### ✅ Tests de Sécurité

#### Test 10.1: Mot de passe masqué par défaut
- **Action**: Entrer un mot de passe
- **Résultat attendu**: Caractères masqués (•••)
- **Statut**: [ ]

#### Test 10.2: Token non visible
- **Action**: Vérifier les logs
- **Résultat attendu**: Token JWT non affiché en clair dans Logcat (sauf debug)
- **Statut**: [ ]

#### Test 10.3: Pas de données sensibles dans les logs
- **Action**: Vérifier Logcat
- **Résultat attendu**: Mots de passe non loggés
- **Statut**: [ ]

## 📊 Rapport de Tests

### Résumé
- **Total de tests**: 40
- **Tests réussis**: ___
- **Tests échoués**: ___
- **Tests non applicables**: ___

### Tests Critiques (Priorité Haute)
- [ ] Test 2.1: Inscription réussie
- [ ] Test 3.1: Connexion réussie
- [ ] Test 4.3: Navigation vers Dashboard
- [ ] Test 5.1: Token persistant
- [ ] Test 7.1: Backend inaccessible

### Bugs Trouvés
| ID | Description | Sévérité | Statut |
|----|-------------|----------|--------|
| 1  |             |          |        |
| 2  |             |          |        |
| 3  |             |          |        |

### Environnement de Test
- **Appareil**: _______________
- **Version Android**: _______________
- **Date**: _______________
- **Testeur**: _______________

## 🎯 Scénarios de Test Complets

### Scénario 1: Premier utilisateur
```
1. Lancer l'app (première fois)
2. Voir l'écran de login
3. Cliquer sur "S'inscrire"
4. Remplir tous les champs
5. S'inscrire avec succès
6. Voir le Dashboard
7. Fermer l'app
8. Rouvrir l'app
9. Toujours sur le Dashboard
```

### Scénario 2: Utilisateur existant
```
1. Lancer l'app
2. Voir l'écran de login
3. Entrer email/password
4. Se connecter
5. Voir le Dashboard
6. Se déconnecter
7. Retour au login
```

### Scénario 3: Erreurs de validation
```
1. Aller sur inscription
2. Entrer email invalide → Erreur
3. Corriger l'email → Erreur disparaît
4. Entrer téléphone invalide → Erreur
5. Corriger le téléphone → Erreur disparaît
6. Mot de passe court → Erreur
7. Corriger → Erreur disparaît
8. Soumettre avec succès
```

### Scénario 4: Gestion réseau
```
1. Arrêter le backend
2. Essayer de se connecter
3. Voir message d'erreur réseau
4. Relancer le backend
5. Réessayer
6. Connexion réussie
```

## 📝 Notes de Test

### Points d'attention
- Vérifier que les messages d'erreur sont en français
- Vérifier que les emojis s'affichent correctement (💪, 🏋️, ✅)
- Vérifier que le gradient des boutons est visible
- Vérifier que les champs en erreur ont une bordure rouge

### Cas limites à tester
- Email très long (> 100 caractères)
- Nom/Prénom avec caractères spéciaux (é, è, ç)
- Téléphone avec espaces (06 12 34 56 78)
- Mot de passe avec caractères spéciaux

### Compatibilité
- [ ] Android 7.0 (API 24)
- [ ] Android 8.0 (API 26)
- [ ] Android 9.0 (API 28)
- [ ] Android 10 (API 29)
- [ ] Android 11 (API 30)
- [ ] Android 12+ (API 31+)

## ✅ Validation Finale

Avant de considérer l'application prête:
- [ ] Tous les tests critiques passent
- [ ] Aucun crash détecté
- [ ] Messages d'erreur clairs et en français
- [ ] Design conforme au thème fitness
- [ ] Performance acceptable
- [ ] Backend accessible depuis l'app
