# 🔍 Diagnostic - Pourquoi Seulement 2 Programmes ?

## 🚨 Causes Possibles

### 1. **Filtre Favoris Actif** ⭐
**Symptôme** : Le bouton cœur en haut à droite est rouge/rempli
**Solution** : Appuyez sur le bouton cœur pour désactiver le filtre favoris

### 2. **Utilisateur N'a Que 2 Programmes Assignés** 👤
**Symptôme** : C'est le comportement normal si l'utilisateur n'est inscrit qu'à 2 programmes
**Vérification** : Allez dans "Programmes" pour voir tous les programmes disponibles

### 3. **Problème de Connexion Backend** 🌐
**Symptôme** : Erreur de chargement ou programmes vides
**Solution** : Vérifier que le serveur backend est démarré sur port 8100

### 4. **Cache ou Token Expiré** 🔑
**Symptôme** : Données anciennes ou erreurs d'authentification
**Solution** : Se déconnecter et se reconnecter

## 🧪 Tests de Diagnostic

### Test 1: Vérifier le Filtre Favoris
1. Ouvrez "Mes Programmes"
2. Regardez le bouton cœur en haut à droite
3. **Si rouge/rempli** → Appuyez dessus pour désactiver le filtre
4. **Si gris/vide** → Le filtre n'est pas actif

### Test 2: Diagnostic Automatique
1. Dans "Mes Programmes", appuyez **2 fois rapidement** sur l'écran (double tap)
2. Un diagnostic automatique se lance
3. Regardez les messages Toast et les logs

### Test 3: Rechargement Forcé
1. Dans "Mes Programmes", **maintenez appuyé** sur l'écran (long press)
2. Un rechargement forcé se lance
3. Vérifiez si plus de programmes apparaissent

### Test 4: Vérifier Tous les Programmes Disponibles
1. Allez dans "Programmes" (depuis le dashboard)
2. Comptez combien de programmes sont disponibles
3. Inscrivez-vous à d'autres programmes si souhaité

## 📱 Actions Recommandées

### Si le Filtre Favoris Était Actif :
✅ **Résolu** - Vous devriez maintenant voir tous vos programmes

### Si Vous N'avez Que 2 Programmes :
1. **Normal** - Allez dans "Programmes" pour vous inscrire à d'autres
2. **Vérifiez** votre compte utilisateur (peut-être un compte de test)

### Si Problème Technique :
1. **Redémarrez** l'application
2. **Déconnectez-vous** et reconnectez-vous
3. **Vérifiez** la connexion internet
4. **Contactez** l'équipe technique avec les logs

## 🔍 Logs à Rechercher

Ouvrez Android Studio et cherchez ces logs :

```
=== DIAGNOSTIC UTILISATEUR ===
- ID: [votre_id]
- Email: [votre_email]

=== DIAGNOSTIC FILTRES ===
- Filtre favoris actif: true/false
- Nombre favoris connus: X
```

### Interprétation :
- **Filtre favoris actif: true** → C'est la cause !
- **Programmes totaux: 2** → Vous n'avez que 2 programmes assignés
- **Erreur HTTP** → Problème de connexion backend

## 🎯 Conclusion Probable

**Dans 90% des cas**, c'est l'une de ces 2 causes :
1. **Filtre favoris actif** (bouton cœur rouge)
2. **Utilisateur n'a réellement que 2 programmes** (comportement normal)

Suivez les tests ci-dessus pour identifier la cause exacte ! 🕵️‍♂️