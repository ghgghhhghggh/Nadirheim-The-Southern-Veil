#version 330

// Portail de Nadirheim (même principe que le portail de l'End, réécrit pour le mod) :
// - Sampler0 = le fond (textures/environment/nadirheim_sky.png), multiplié par SKY_TINT (assombrit et recolore) ;
// - Sampler1 = la couche de particules (textures/entity/nadirheim_portal/...), en gris : le noir n'ajoute rien,
//   plus un pixel est clair plus il brille ;
// - la couche de particules est empilée PORTAL_LAYERS fois, chaque copie avec sa taille, son angle, sa vitesse
//   et sa couleur : les petites copies semblent lointaines, les grandes proches -> impression de profondeur.
// Les couleurs se règlent ici : SKY_TINT pour le fond, FAR_COLOR pour les couches lointaines, NEAR_COLOR pour les
// proches (rouge, vert, bleu entre 0 et 1). Modifier ce fichier ne demande pas de relancer le jeu : ./gradlew
// processResources puis F3+T en jeu.

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:globals.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec4 screenPos;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

// Bleu clair (2e essai). 1er essai, turquoise sombre : fond x 0.15, FAR (0.02, 0.09, 0.09), NEAR (0.15, 0.50, 0.55)
const vec3 SKY_TINT = vec3(0.15, 0.28, 0.45);
// Paillettes d'or : les pixels les plus sombres du fond (luminosité sous GOLD_LIGHT, en or plein sous GOLD_DARK)
// deviennent dorés. Elles sont lues dans une copie du fond GOLD_SCALE fois plus serrée (donc plus petites que
// les pixels du fond) et mélangées au bleu à GOLD_STRENGTH au maximum (1 = or pur, 0 = invisible).
// Parmi ces pixels sombres, seule une part GOLD_KEEP (tirée au hasard, toujours la même par pixel) devient dorée.
// 1er essai jugé trop contrasté : or (0.85, 0.60, 0.15) plein, seuils 0.32 / 0.45, taille du fond.
// 2e essai (seuils 0.27 / 0.40, tout gardé) : trop de points avec le ciel actuel, dont la teinte la plus sombre
// (#304A47, luminosité ~0.26) couvre 1 pixel sur 8 -> maintenant seule cette teinte compte, et 1 sur 2 est gardé.
const vec3 GOLD_COLOR = vec3(0.90, 0.76, 0.40);
const float GOLD_DARK = 0.27;
const float GOLD_LIGHT = 0.30;
const float GOLD_KEEP = 0.5;
const float GOLD_SCALE = 2.0;
const float GOLD_STRENGTH = 0.65;
const vec3 FAR_COLOR = vec3(0.04, 0.10, 0.18);
const vec3 NEAR_COLOR = vec3(0.30, 0.60, 0.95);

void main() {
    // Position à l'écran (-1..1 en hauteur), corrigée pour que les particules restent rondes sur un écran large
    vec2 p = screenPos.xy / screenPos.w;
    p.x *= ScreenSize.x / ScreenSize.y;

    vec3 sky = texture(Sampler0, p * 0.5 + 0.5).rgb;
    vec2 goldUv = p * 0.5 * GOLD_SCALE + vec2(0.37, 0.61);
    vec3 goldSource = texture(Sampler0, goldUv).rgb;
    float goldLight = dot(goldSource, vec3(0.299, 0.587, 0.114));
    float gold = 1.0 - smoothstep(GOLD_DARK, GOLD_LIGHT, goldLight);   // 1 = pixel très sombre, 0 = pixel normal
    // Tirage au sort par pixel du fond (même résultat à chaque image, donc pas de clignotement)
    vec2 texel = floor(goldUv * vec2(textureSize(Sampler0, 0)));
    float lottery = fract(sin(dot(texel, vec2(12.9898, 78.233))) * 43758.5453);
    gold *= step(lottery, GOLD_KEEP);
    vec3 color = mix(sky * SKY_TINT, GOLD_COLOR, gold * GOLD_STRENGTH);

    for (int i = 0; i < PORTAL_LAYERS; i++) {
        float depth = float(i) / float(PORTAL_LAYERS - 1);   // 0 = couche la plus lointaine, 1 = la plus proche
        float angle = float(i * i) * 2.399 + float(i) * 0.7;  // un angle différent pour chaque couche
        mat2 rotation = mat2(cos(angle), sin(angle), -sin(angle), cos(angle));
        float scale = mix(2.2, 0.4, depth);                   // lointaine = motif répété plus souvent = plus petit
        // GameTime va de 0 à 1 sur une journée Minecraft (20 min) : les couches dérivent lentement, à des vitesses
        // différentes
        vec2 drift = vec2(17.0 / float(i + 1), GameTime * (20.0 + 2.0 * float(i)));
        vec2 uv = rotation * (p * scale) + drift;
        color += texture(Sampler1, uv).rgb * mix(FAR_COLOR, NEAR_COLOR, depth);
    }

    fragColor = apply_fog(vec4(color, 1.0), sphericalVertexDistance, cylindricalVertexDistance,
            FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, FogColor);
}
