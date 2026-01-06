# 🔍 DEBUG - Problème Champs Inscription

## Problème Identifié
Le backend renvoie l'erreur : "Le numéro de téléphone est obligatoire, L'adresse email est obligatoire"

## Données Envoyées (Dernière Tentative)
```json
{
  "email": "sameh@gmail.com",
  "dateNaissance": "2008-01-01", 
  "motDePasse": "Sameh123.",
  "nom": "abir",
  "numeroTelephone": "0911111111",
  "prenom": "sameh"
}
```

## Solutions à Tester

### Option 1: Noms de Champs Standards
```kotlin
@SerializedName("telephone") // ou "phone"
@SerializedName("email") // ou "adresseEmail"
```

### Option 2: Noms de Champs Français
```kotlin
@SerializedName("numeroTelephone")
@SerializedName("adresseEmail")
```

### Option 3: Noms de Champs Courts
```kotlin
@SerializedName("numtel")
@SerializedName("adresseemail")
```

## Action Recommandée
Vérifier la documentation backend ou tester avec un outil comme Postman pour identifier les noms exacts attendus.

## Test Backend Direct
Essayez cette requête curl pour tester directement :

```bash
curl -X POST http://10.0.2.2:8100/api/auth/inscription \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "test",
    "prenom": "test", 
    "telephone": "0123456789",
    "email": "test@test.com",
    "motDePasse": "Test123.",
    "dateNaissance": "1990-01-01"
  }'
```

Si cela ne fonctionne pas, essayez avec d'autres noms de champs.