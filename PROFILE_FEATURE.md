# 👤 Fonctionnalité Profil Utilisateur

## 📋 Description

Interface de profil utilisateur accessible depuis le Dashboard permettant de visualiser les informations personnelles de l'utilisateur.

---

## 🎯 Fonctionnalités

### ✅ Affichage des Informations
- **Avatar** : Icône utilisateur avec design circulaire
- **Nom complet** : Prénom + Nom
- **Email** : Adresse email de l'utilisateur
- **Informations détaillées** :
  - Nom
  - Prénom
  - Email

### ✅ Statistiques (Placeholder)
- Nombre de programmes
- Nombre de plats
- Nombre de jours actifs

### ✅ Actions
- **Retour** : Bouton pour revenir au Dashboard
- **Déconnexion** : Bouton pour se déconnecter

---

## 🎨 Design

### Palette de Couleurs
- **Header** : Dégradé vert (organic_primary → organic_primary_light)
- **Background** : Fond clair (organic_background)
- **Cards** : Blanc (organic_surface)
- **Texte** : Gris foncé (organic_text_primary)

### Éléments Visuels
- Avatar circulaire avec élévation
- Cartes arrondies (radius: 16-20dp)
- Séparateurs subtils entre les informations
- Bouton de déconnexion avec fond vert

---

## 📱 Navigation

### Accès au Profil
```
Dashboard → Clic sur "User Profile Card" → ProfileActivity
```

### Depuis le Profil
- **Bouton Retour** → Retour au Dashboard
- **Bouton Déconnexion** → LoginActivity (avec clear de la pile)

---

## 🔧 Implémentation Technique

### Activity
```kotlin
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var preferencesManager: PreferencesManager
    
    // Affiche les informations depuis PreferencesManager
    // Gère la déconnexion
}
```

### Layout
- **ScrollView** : Pour supporter différentes tailles d'écran
- **ConstraintLayout** : Pour un positionnement flexible
- **CardView** : Pour les sections d'information et statistiques

### Données Affichées
Les données proviennent de `PreferencesManager`:
- `getUserNom()`
- `getUserPrenom()`
- `getUserEmail()`

---

## 📂 Fichiers Créés

```
app/src/main/java/com/example/projetintegration/ui/activities/
└── ProfileActivity.kt

app/src/main/res/layout/
└── activity_profile.xml
```

---

## 🔄 Flux de Données

```
1. Utilisateur clique sur la carte de profil dans Dashboard
   └─> Intent vers ProfileActivity

2. ProfileActivity démarre
   └─> Récupère les données depuis PreferencesManager
   └─> Affiche les informations

3. Utilisateur clique sur Déconnexion
   └─> PreferencesManager.clearAuthData()
   └─> Navigation vers LoginActivity
   └─> Clear de la pile d'activités
```

---

## 🎯 Améliorations Futures

### Phase 2 (Optionnel)
- [ ] Édition du profil
- [ ] Upload de photo de profil
- [ ] Modification du mot de passe
- [ ] Statistiques réelles depuis le backend
- [ ] Affichage de la date d'inscription
- [ ] Affichage du numéro de téléphone
- [ ] Affichage de la date de naissance

### Phase 3 (Optionnel)
- [ ] Paramètres de l'application
- [ ] Préférences de notification
- [ ] Thème clair/sombre
- [ ] Langue de l'application
- [ ] Suppression du compte

---

## 📸 Structure Visuelle

```
┌─────────────────────────────────────┐
│  ← Mon Profil                       │ ← Header vert
│                                     │
│         ┌─────────┐                 │
│         │   👤    │                 │ ← Avatar
│         └─────────┘                 │
│                                     │
│      Prénom Nom                     │
│   email@example.com                 │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │ Informations Personnelles   │   │
│  │ ─────                       │   │
│  │ Nom:        Doe             │   │
│  │ ─────────────────────────   │   │
│  │ Prénom:     John            │   │
│  │ ─────────────────────────   │   │
│  │ Email:      john@email.com  │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ Statistiques                │   │
│  │ ─────                       │   │
│  │   0          0          0   │   │
│  │ Programmes  Plats     Jours │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │      Déconnexion            │   │ ← Bouton vert
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## ✅ Checklist d'Implémentation

- [x] Créer ProfileActivity.kt
- [x] Créer activity_profile.xml
- [x] Ajouter la navigation depuis Dashboard
- [x] Rendre la carte de profil cliquable
- [x] Déclarer l'Activity dans AndroidManifest.xml
- [x] Implémenter l'affichage des informations
- [x] Implémenter la déconnexion
- [x] Tester la navigation
- [x] Vérifier la compilation

---

## 🔒 Sécurité

- ✅ Vérification de la connexion au démarrage
- ✅ Redirection vers Login si non connecté
- ✅ Clear complet des données lors de la déconnexion
- ✅ Clear de la pile d'activités après déconnexion

---

**Version**: 1.0  
**Dernière mise à jour**: 2024  
**Statut**: ✅ Implémenté et fonctionnel
