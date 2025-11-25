# Guide d'Installation des Images

## 📁 Emplacement des Images

Placez **TOUTES** les images dans le dossier:
```
app/src/main/res/drawable/
```

## 📋 Liste des Images et Leurs Utilisations

### 1. **authentificationetinscription.png**
- **Emplacement**: `app/src/main/res/drawable/authentificationetinscription.png`
- **Utilisation**: Logo sur les pages de connexion et d'inscription
- **Taille recommandée**: 120x120px

### 2. **applesplash.png**
- **Emplacement**: `app/src/main/res/drawable/applesplash.png`
- **Utilisation**: 
  - Logo dans la barre de navigation du Dashboard
  - Image pour les plats de catégorie "collation"
- **Taille recommandée**: 40x40px (navbar), 80x80px (plats)

### 3. **platsicon.png**
- **Emplacement**: `app/src/main/res/drawable/platsicon.png`
- **Utilisation**: 
  - Icône de la carte "Nos Plats" sur le Dashboard
  - Icône dans le header de la page Plats
- **Taille recommandée**: 64x64px

### 4. **programmeicon.png**
- **Emplacement**: `app/src/main/res/drawable/programmeicon.png`
- **Utilisation**: 
  - Icône de la carte "Programmes" sur le Dashboard
  - Icône dans le header de la page Programmes
  - Image dans le détail d'un programme
- **Taille recommandée**: 64x64px (dashboard), 120x120px (détail)

### 5. **mesprogrammesicon.png**
- **Emplacement**: `app/src/main/res/drawable/mesprogrammesicon.png`
- **Utilisation**: 
  - Icône de la carte "Mes Programmes" sur le Dashboard
  - Icône dans le header de la page Mes Programmes
- **Taille recommandée**: 64x64px

### 6. **omelette.png**
- **Emplacement**: `app/src/main/res/drawable/omelette.png`
- **Utilisation**: Image pour les plats de catégorie "petit-dejeuner"
- **Taille recommandée**: 80x80px

### 7. **plat_meat.png**
- **Emplacement**: `app/src/main/res/drawable/plat_meat.png`
- **Utilisation**: Image pour les plats de catégorie "dejeuner"
- **Taille recommandée**: 80x80px

### 8. **tacos.png**
- **Emplacement**: `app/src/main/res/drawable/tacos.png`
- **Utilisation**: Image pour les plats de catégorie "diner"
- **Taille recommandée**: 80x80px

### 9. **vegetables.png**
- **Emplacement**: `app/src/main/res/drawable/vegetables.png`
- **Utilisation**: 
  - Image par défaut pour les plats
  - Image dans le détail d'un plat
- **Taille recommandée**: 80x80px (liste), 120x120px (détail)

### 10. **sala1d.png** (Non utilisée actuellement)
- **Emplacement**: `app/src/main/res/drawable/sala1d.png`
- **Utilisation**: Disponible pour utilisation future

### 11. **salad2.png** (Non utilisée actuellement)
- **Emplacement**: `app/src/main/res/drawable/salad2.png`
- **Utilisation**: Disponible pour utilisation future

## 🔧 Instructions d'Installation

### Étape 1: Copier les Images
1. Ouvrez le dossier de votre projet Android
2. Naviguez vers `app/src/main/res/drawable/`
3. Copiez toutes les images PNG dans ce dossier

### Étape 2: Vérifier les Noms
Assurez-vous que les noms des fichiers sont **exactement** comme suit (en minuscules):
- ✅ `authentificationetinscription.png`
- ✅ `applesplash.png`
- ✅ `platsicon.png`
- ✅ `programmeicon.png`
- ✅ `mesprogrammesicon.png`
- ✅ `omelette.png`
- ✅ `plat_meat.png`
- ✅ `tacos.png`
- ✅ `vegetables.png`
- ✅ `sala1d.png`
- ✅ `salad2.png`

### Étape 3: Rebuild le Projet
1. Dans Android Studio, cliquez sur `Build` > `Clean Project`
2. Puis `Build` > `Rebuild Project`
3. Attendez que la compilation se termine

## 📱 Mapping des Images par Écran

### Dashboard (Accueil)
```
┌─────────────────────────────┐
│ FitLife    [apple_splash]   │ ← Logo navbar
├─────────────────────────────┤
│ Bienvenue                   │
│                             │
│ ┌─────────────────────────┐ │
│ │ [platsicon] Nos Plats   │ │ ← Carte Plats
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ [programmeicon] Prog... │ │ ← Carte Programmes
│ └─────────────────────────┘ │
│ ┌─────────────────────────┐ │
│ │ [mesprogrammesicon] ... │ │ ← Carte Mes Programmes
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

### Page Login/Signup
```
┌─────────────────────────────┐
│  [authentificationetinsc]   │ ← Logo
│      Bienvenue              │
│                             │
│  [Email Input]              │
│  [Password Input]           │
│  [Button]                   │
└─────────────────────────────┘
```

### Page Liste des Plats
```
┌─────────────────────────────┐
│ ← Nos Plats  [platsicon]    │ ← Header
├─────────────────────────────┤
│ [Filtres]                   │
├─────────────────────────────┤
│ ┌──────┐  ┌──────┐          │
│ │[omel]│  │[meat]│          │ ← Images selon catégorie
│ │Plat 1│  │Plat 2│          │
│ └──────┘  └──────┘          │
└─────────────────────────────┘
```

## 🎨 Catégories de Plats et Images

| Catégorie        | Image Utilisée    |
|------------------|-------------------|
| petit-dejeuner   | omelette.png      |
| dejeuner         | plat_meat.png     |
| diner            | tacos.png         |
| collation        | applesplash.png   |
| Par défaut       | vegetables.png    |

## ⚠️ Notes Importantes

1. **Format**: Utilisez uniquement des fichiers PNG
2. **Noms**: Les noms doivent être en minuscules avec underscores (pas de tirets, pas d'espaces)
3. **Taille**: Les images seront redimensionnées automatiquement, mais pour de meilleures performances:
   - Icônes: 64x64px ou 128x128px
   - Images de plats: 200x200px maximum
4. **Transparence**: Les PNG avec transparence sont supportés

## 🔄 Si les Images ne S'Affichent Pas

1. Vérifiez que les noms de fichiers sont corrects
2. Nettoyez le projet: `Build` > `Clean Project`
3. Reconstruisez: `Build` > `Rebuild Project`
4. Invalidez le cache: `File` > `Invalidate Caches / Restart`
5. Vérifiez que les images sont bien dans `app/src/main/res/drawable/`

## ✅ Checklist

- [ ] Toutes les images sont copiées dans `app/src/main/res/drawable/`
- [ ] Les noms de fichiers sont corrects (minuscules, underscores)
- [ ] Le projet a été nettoyé et reconstruit
- [ ] Les images s'affichent correctement dans l'application

---

**Dernière mise à jour**: 2024
