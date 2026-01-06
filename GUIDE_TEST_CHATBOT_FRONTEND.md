# 🧪 Guide de Test - ChatBot Frontend

## ✅ Checklist de Vérification

### 1. Compilation et Build
```bash
# Vérifier que le projet compile sans erreurs
./gradlew build
```

### 2. Navigation depuis le Dashboard
- [ ] Ouvrir l'application
- [ ] Se connecter avec un utilisateur existant
- [ ] Vérifier que la carte "Assistant Santé 🤖" est visible
- [ ] Cliquer sur la carte → doit ouvrir `ChatBotConversationsActivity`

### 3. Liste des Conversations
- [ ] L'écran "💬 Mes Conversations" s'affiche
- [ ] Le bouton "+" (FloatingActionButton) est visible
- [ ] Pull-to-refresh fonctionne
- [ ] Si aucune conversation → liste vide
- [ ] Cliquer sur "+" → ouvre une nouvelle conversation

### 4. Interface de Chat
- [ ] L'écran de chat s'ouvre avec le titre "Nouvelle conversation"
- [ ] Zone de saisie de message visible en bas
- [ ] Bouton d'envoi (📤) visible
- [ ] Taper un message et appuyer sur envoyer
- [ ] Vérifier que le message utilisateur s'affiche (bulle bleue à droite)
- [ ] Vérifier l'indicateur "L'assistant écrit..." pendant le chargement
- [ ] Vérifier que la réponse de l'assistant s'affiche (bulle grise à gauche avec 🤖)

### 5. Test avec le Backend
Assurez-vous que le backend ChatBot est démarré sur le port 8100.

#### Messages de Test Recommandés :
1. **"Bonjour, j'ai besoin de conseils nutritionnels"**
2. **"Peux-tu me créer un programme d'exercices ?"**
3. **"Comment améliorer mon alimentation ?"**
4. **"Quels exercices pour perdre du poids ?"**

### 6. Gestion des Erreurs
- [ ] Tester sans connexion internet → message d'erreur approprié
- [ ] Tester avec backend arrêté → message d'erreur approprié
- [ ] Tester avec token expiré → gestion de l'authentification

## 🔧 Résolution des Problèmes Courants

### Erreur de Compilation
Si vous avez des erreurs de compilation :
1. Vérifiez que tous les imports sont corrects
2. Synchronisez le projet (Sync Project with Gradle Files)
3. Clean et rebuild : `./gradlew clean build`

### Erreur de Navigation
Si le clic sur la carte ChatBot ne fonctionne pas :
1. Vérifiez que les activités sont déclarées dans `AndroidManifest.xml`
2. Vérifiez l'import de `ChatBotConversationsActivity` dans `DashboardActivity`

### Erreur API
Si les appels API échouent :
1. Vérifiez que le backend est démarré sur `http://localhost:8100`
2. Vérifiez que l'émulateur utilise `http://10.0.2.2:8100`
3. Vérifiez les logs avec `adb logcat`

### Erreur d'Authentification
Si vous avez des erreurs 401/403 :
1. Vérifiez que l'utilisateur est bien connecté
2. Vérifiez que le token JWT est valide
3. Testez d'abord avec Postman/curl

## 📱 Test Manuel Complet

### Scénario 1 : Première Utilisation
1. Ouvrir l'app et se connecter
2. Aller au Dashboard
3. Cliquer sur "Assistant Santé"
4. Vérifier que la liste est vide
5. Cliquer sur "+" pour nouvelle conversation
6. Envoyer le message : "Bonjour, j'ai besoin d'aide"
7. Vérifier la réponse de l'assistant
8. Retourner à la liste des conversations
9. Vérifier qu'une nouvelle conversation apparaît

### Scénario 2 : Conversation Existante
1. Depuis la liste des conversations
2. Cliquer sur une conversation existante
3. Vérifier que l'historique se charge
4. Envoyer un nouveau message
5. Vérifier que le contexte est préservé

### Scénario 3 : Multiples Conversations
1. Créer plusieurs conversations avec des sujets différents
2. Vérifier que chaque conversation a son propre historique
3. Tester la navigation entre les conversations

## 🚀 Optimisations Futures

Une fois les tests de base réussis, vous pouvez ajouter :

### Fonctionnalités Bonus
- [ ] Suggestions de questions de démarrage
- [ ] Copier/Partager les messages
- [ ] Recherche dans l'historique
- [ ] Suppression de conversations
- [ ] Mode sombre
- [ ] Notifications push pour les réponses

### Améliorations UX
- [ ] Animation de frappe plus réaliste
- [ ] Scroll automatique fluide
- [ ] Indicateurs de lecture
- [ ] Timestamps plus lisibles
- [ ] Avatars personnalisés

## 📊 Métriques de Performance

### Temps de Réponse Attendus
- **Chargement liste conversations :** < 2 secondes
- **Chargement conversation :** < 1 seconde
- **Envoi message :** < 5 secondes (dépend de l'IA)
- **Navigation entre écrans :** < 500ms

### Utilisation Mémoire
- Surveiller les fuites mémoire avec les RecyclerViews
- Optimiser le chargement des images/emojis
- Gérer la pagination pour les longues conversations

---

## 🎉 Félicitations !

Si tous les tests passent, vous avez maintenant un ChatBot fonctionnel intégré à votre application HealthyApp ! 

L'assistant peut maintenant aider vos utilisateurs avec :
- 🥗 Conseils nutritionnels personnalisés
- 💪 Programmes d'exercices adaptés
- 🏃 Motivation et suivi des progrès
- 🧘 Conseils bien-être général

**Prochaine étape :** Déployer en production et collecter les retours utilisateurs pour améliorer l'expérience !