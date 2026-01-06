# 🚀 Guide d'Utilisation Rapide - Application FitLife

## ⚠️ IMPORTANT: Ordre des Étapes

Pour utiliser l'application correctement, suivez cet ordre:

---

## 📋 ÉTAPE 1: S'inscrire à un Programme

### Pourquoi?
Vous devez d'abord vous inscrire à un programme avant de pouvoir marquer votre progression quotidienne.

### Comment?

1. **Ouvrir "Programmes"** depuis le Dashboard
2. **Parcourir les programmes** disponibles
3. **Cliquer sur un programme** pour voir les détails
4. **Cliquer sur "S'inscrire"** ou "Commencer ce programme"
5. **Remplir le formulaire:**
   - Date de début (par défaut: aujourd'hui)
   - Poids actuel (optionnel)
   - Poids objectif (optionnel)
6. **Valider l'inscription**

### Résultat:
✅ Vous avez maintenant un **programme actif**!

---

## 📊 ÉTAPE 2: Marquer sa Progression Quotidienne

### Maintenant que vous avez un programme actif:

1. **Ouvrir "Mes Programmes"** depuis le Dashboard
2. **Cliquer sur votre programme actif**
3. **Cocher les repas** que vous avez consommés aujourd'hui
4. **Cocher les activités** que vous avez réalisées
5. **Cliquer sur "✅ ENREGISTRER MA JOURNÉE"**

### Résultat:
✅ Votre progression est enregistrée!
✅ Le backend recalcule automatiquement votre progression globale!

---

## 🔄 Flux Complet

```
1. Inscription
   Dashboard → Programmes → Choisir un programme → S'inscrire
   
2. Progression Quotidienne
   Dashboard → Mes Programmes → Cliquer sur le programme
   → Cocher plats/activités → Enregistrer ma journée
   
3. Voir les Statistiques
   Dashboard → Mes Programmes → Cliquer sur le programme
   → Voir Statistiques Détaillées
```

---

## ❌ Erreurs Courantes

### "Aucun programme actif trouvé"
**Cause:** Vous n'êtes inscrit à aucun programme
**Solution:** Allez dans "Programmes" et inscrivez-vous à un programme

### "404 Not Found"
**Cause:** L'endpoint n'existe pas ou le backend n'est pas démarré
**Solution:** Vérifiez que le backend est bien lancé sur `http://10.0.2.2:8091`

### "400 Bad Request"
**Cause:** Vous essayez d'accéder à des données sans avoir de programme actif
**Solution:** Inscrivez-vous d'abord à un programme

---

## 📱 Navigation dans l'App

### Dashboard (Page d'accueil)
- **Plats** → Voir tous les plats disponibles
- **Programmes** → Voir et s'inscrire aux programmes
- **Mes Programmes** → Voir vos programmes et marquer la progression
- **Profil** → Gérer votre profil

### Programmes
- Liste de tous les programmes disponibles
- Filtres par objectif (Perte de poids, Prise de masse, etc.)
- Cliquer sur un programme pour voir les détails
- Bouton "S'inscrire" pour commencer

### Mes Programmes
- Liste de vos programmes (actifs, terminés, en pause)
- Cliquer sur un programme pour:
  - Voir les détails
  - Marquer la progression quotidienne
  - Voir les statistiques

### Mon Programme (Détails)
- **En haut:** Infos du programme + progression globale
- **Au milieu:** Liste des plats avec checkbox
- **En bas:** Liste des activités avec checkbox
- **Bouton principal:** "✅ ENREGISTRER MA JOURNÉE"

---

## 🎯 Calcul de la Progression

### Automatique!
La progression est calculée **automatiquement** par le backend selon:

```
Progression = (Taux Complétion × 40%) +
              (Taux Repas × 30%) +
              (Taux Activités × 20%) +
              (Évolution Physique × 10%)
```

### Vous n'avez rien à faire!
- Cochez vos plats et activités
- Cliquez sur "Enregistrer ma journée"
- Le backend fait le reste!

---

## 💡 Conseils

1. **Enregistrez quotidiennement** pour une progression précise
2. **Soyez honnête** avec vos repas et activités
3. **Pesez-vous régulièrement** pour suivre votre évolution
4. **Consultez vos statistiques** pour rester motivé
5. **Maintenez votre streak** (série de jours consécutifs)

---

## 🐛 Problème Actuel (d'après les logs)

### Ce qui se passe:
```
GET /api/programmes/actif → 404
{"message":"Aucun programme actif trouvé"}
```

### Pourquoi?
Vous n'êtes **pas encore inscrit** à un programme.

### Solution:
1. Allez dans **"Programmes"**
2. Choisissez un programme
3. Cliquez sur **"S'inscrire"**
4. Remplissez le formulaire
5. Validez

### Après l'inscription:
✅ `GET /api/programmes/actif` → 200 OK
✅ Vous pourrez marquer votre progression
✅ Les statistiques seront disponibles

---

## 📞 Support

Si vous rencontrez des problèmes:
1. Vérifiez que le backend est lancé
2. Vérifiez que vous êtes bien connecté
3. Vérifiez que vous êtes inscrit à un programme
4. Consultez les logs pour plus de détails

---

## ✅ Checklist de Démarrage

- [ ] Backend lancé sur `http://10.0.2.2:8091`
- [ ] Compte créé et connecté
- [ ] Inscrit à un programme
- [ ] Première progression enregistrée
- [ ] Statistiques visibles

Une fois ces étapes complétées, vous pouvez utiliser l'app normalement! 🎉
