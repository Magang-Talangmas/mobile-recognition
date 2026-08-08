import os

filepath = r"d:\JavaTraining\app\src\main\res\layout\activity_profile.xml"
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

replacements = {
    'android:background="#f4faff"': 'android:background="@color/ent_background"',
    'android:textColor="#000666"': 'android:textColor="@color/ent_primary"',
    'app:cardBackgroundColor="#ffffff"': 'app:cardBackgroundColor="@color/ent_surface"',
    'app:strokeColor="#c6c5d4"': 'app:strokeColor="@color/ent_outline"',
    'app:cardElevation="0dp"': 'app:cardElevation="8dp"',
    'app:strokeWidth="1dp"': 'app:strokeWidth="0dp"',
    'app:cardCornerRadius="12dp"': 'app:cardCornerRadius="16dp"',
    'android:textColor="#454652"': 'android:textColor="@color/ent_text_secondary"',
    'android:background="#331a237e"': 'android:background="@android:color/transparent"',
    'android:textColor="#111d23"': 'android:textColor="@color/ent_text_primary"',
    'app:tint="#835400"': 'app:tint="@color/ent_secondary_dark"',
    'app:tint="#000666"': 'app:tint="@color/ent_primary"',
    'app:tint="#111d23"': 'app:tint="@color/ent_text_primary"',
    'android:background="#e3f0f8"': 'android:background="@color/ent_outline"',
    'app:strokeColor="#000666"': 'app:strokeColor="@color/ent_primary"',
    'app:iconTint="#000666"': 'app:iconTint="@color/ent_primary"',
    'android:backgroundTint="#e9f6fd"': 'android:backgroundTint="@color/ent_surface_variant"',
    'android:backgroundTint="#e5eeff"': 'android:backgroundTint="@color/ent_surface_variant"',
    'app:trackTint="#835400"': 'app:trackTint="@color/ent_secondary"',
}

for old, new in replacements.items():
    content = content.replace(old, new)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Done")
