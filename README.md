# TP Android — Liste de contacts (Version 2)

Application Android complète développée en **Kotlin** comportant deux activités interconnectées, une `ListView` personnalisée, et l'utilisation d'Intents explicites et implicites.

## 📱 Fonctionnalités

### 1. Écran Principal (`MainActivity`)
- **ListView personnalisée** : Chaque contact affiche sa photo de profil circulaire, son nom complet en gras, son numéro de téléphone et un indicateur chevron.
- **Ajout de contact** : Bouton `+` dans la barre d'outils ouvrant `DetailActivity` en mode création.
- **Navigation vers le détail** : Clic sur n'importe quel contact ouvrant `DetailActivity` avec passage de données via `Intent` explicite et attente de résultat (`registerForActivityResult`).

### 2. Écran Détail (`DetailActivity`)
- **Modification des informations** : Modification du nom, prénom, numéro de téléphone et adresse e-mail.
- **Intents implicites** :
  - 📞 **Bouton Appeler** : Ouvre le composeur téléphonique (`Intent.ACTION_DIAL`) avec le numéro prérempli.
  - ✉️ **Bouton E-mail** : Ouvre l'application de messagerie (`Intent.ACTION_SENDTO`) avec le destinataire prérempli.
- **Gestion des photos** :
  - 🖼️ **Bouton Choisir une photo** : Sélection d'une image depuis la galerie (`ActivityResultContracts.GetContent`).
  - 📷 **Bouton Prendre une photo** : Prise de vue directe avec l'appareil photo (`ActivityResultContracts.TakePicturePreview`).
- **Retour de résultat** :
  - ✅ **Bouton Enregistrer** : Validation des champs obligatoires, renvoi des données modifiées à `MainActivity` (`RESULT_OK`), et mise à jour instantanée de la liste.
  - ❌ **Bouton Annuler & Flèche retour** : Retour à la liste sans modification (`RESULT_CANCELED`).
  - 🗑️ **Suppression de contact** : Menu corbeille avec boîte de dialogue de confirmation (`RESULT_DELETE`).

### 3. Robustesse & Gestion des erreurs
- Contrôle des champs obligatoires (nom, prénom, téléphone) avec affichage des erreurs `.error`.
- Gestion de l'annulation de la sélection ou de la capture photo sans plantage.
- Sécurisation des intents implicites avec `try / catch (ActivityNotFoundException)`.

---

## 🛠️ Configuration technique
- **Langage** : Kotlin
- **SDK Min** : API 24 (Android 7.0)
- **Target SDK** : API 37
- **Composants** : Material Components 3, ViewCompat Edge-to-Edge, CardView (découpe circulaire)
- **Architecture de build** : Gradle avec Kotlin DSL (`build.gradle.kts`) et Version Catalog (`libs.versions.toml`)