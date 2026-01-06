# Backend Verification Checklist

## 1. Check User Program Data in Database

### Query to run:
```sql
-- Check your user program details
SELECT 
    up.id,
    up.date_debut,
    up.date_fin_prevue,
    up.statut,
    p.nom as programme_nom,
    p.duree_jours,
    u.adresse_email
FROM user_programmes up
JOIN programmes p ON up.programme_id = p.id
JOIN users u ON up.user_id = u.id
WHERE u.adresse_email = 'abir@gmail.com' 
ORDER BY up.date_debut DESC;
```

### What to check:
- ✅ `date_debut` should be a valid date (format: YYYY-MM-DD)
- ✅ `date_fin_prevue` should be after `date_debut`
- ✅ `statut` should be 'EN_COURS'
- ✅ The date range should make sense with `duree_jours`

## 2. Check Backend Validation Logic

### Find the progression controller:
Look for the endpoint `/api/progression/enregistrer` in your backend code.

### Common issues to check:

#### A. Date validation logic
```java
// Check if this logic is too strict
LocalDate dateDebut = userProgramme.getDateDebut();
LocalDate dateFinPrevue = userProgramme.getDateFinPrevue();
LocalDate dateProgression = LocalDate.parse(request.getDate());

if (dateProgression.isBefore(dateDebut) || dateProgression.isAfter(dateFinPrevue)) {
    throw new BadRequestException("Date hors du programme");
}
```

#### B. Timezone issues
```java
// Check if backend uses different timezone
LocalDate.now() // Might be different from frontend date
```

#### C. Program status validation
```java
// Check if program status validation is correct
if (!"EN_COURS".equals(userProgramme.getStatut())) {
    throw new BadRequestException("Programme non actif");
}
```

## 3. Test Backend Directly

### Using curl or Postman:
```bash
# Test with the exact same data
curl -X POST http://10.0.2.2:8100/api/progression/enregistrer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "date": "2025-01-30",
    "platIds": [1],
    "activiteIds": [1]
  }'
```

### Expected responses:
- ✅ **200 OK**: Progression saved successfully
- ❌ **400 Bad Request**: Check the error message
- ❌ **401 Unauthorized**: Token issue
- ❌ **403 Forbidden**: Permission issue

## 4. Backend Logs to Check

### Enable debug logging for:
```properties
# In application.properties or logback.xml
logging.level.com.yourpackage.controller.ProgressionController=DEBUG
logging.level.com.yourpackage.service.ProgressionService=DEBUG
```

### Look for logs showing:
- Received request data
- User program validation
- Date validation logic
- Exact error cause

## 5. Quick Backend Fixes

### If date validation is too strict:
```java
// Option 1: Allow current date if within program duration
LocalDate today = LocalDate.now();
LocalDate programStart = userProgramme.getDateDebut();
LocalDate programEnd = programStart.plusDays(programme.getDureeJours());

// Option 2: Allow any date within program range
if (dateProgression.isBefore(programStart) || dateProgression.isAfter(programEnd)) {
    // More flexible validation
}
```

### If timezone is the issue:
```java
// Use consistent timezone
LocalDate dateProgression = LocalDate.parse(request.getDate());
// Instead of LocalDate.now(), use:
LocalDate today = LocalDate.now(ZoneId.of("UTC"));
```

## 6. Database Fixes

### If program dates are wrong:
```sql
-- Update program end date based on duration
UPDATE user_programmes up
JOIN programmes p ON up.programme_id = p.id
SET up.date_fin_prevue = DATE_ADD(up.date_debut, INTERVAL p.duree_jours DAY)
WHERE up.statut = 'EN_COURS';
```

### If program status is wrong:
```sql
-- Ensure your program is active
UPDATE user_programmes 
SET statut = 'EN_COURS'
WHERE user_id = YOUR_USER_ID 
AND statut != 'TERMINE';
```

## 7. Priority Order

1. **First**: Test the frontend fix - it might solve everything
2. **If still failing**: Check the database query above
3. **If data looks good**: Check backend validation logic
4. **If logic is strict**: Apply backend fixes
5. **Last resort**: Test with Postman to isolate the issue

## 8. What to Share

After checking, please share:
- The database query results
- The exact error message from backend logs
- Whether the frontend fix worked or not

This will help determine if we need backend changes or if the frontend fix is sufficient.