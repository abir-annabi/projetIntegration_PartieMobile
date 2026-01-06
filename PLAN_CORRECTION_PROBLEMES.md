# 🔧 Plan de Correction des Problèmes

## 🎯 Problèmes Identifiés et Solutions

### 1. 🔴 PROBLÈME CRITIQUE: Erreurs 403 (Backend)

**Cause:** Migrations Hibernate échouent au démarrage du backend

**Solution Backend (PRIORITAIRE):**

```sql
-- Se connecter à PostgreSQL
psql -U postgres -d votre_base_de_donnees

-- Corriger les colonnes NULL
UPDATE user_programmes 
SET date_fin_prevue = date_debut + INTERVAL '30 days' 
WHERE date_fin_prevue IS NULL;

UPDATE activites_sportives 
SET type = 'CARDIO' 
WHERE type IS NULL;

UPDATE activites_sportives 
SET duree_minutes = 30 
WHERE duree_minutes IS NULL;

-- Redémarrer le backend
```

### 2. 🟡 PROBLÈME: Port Backend Incorrect

**Problème:** RetrofitClient utilise le port 8091 mais le backend semble sur 8086

**Solution Frontend:**