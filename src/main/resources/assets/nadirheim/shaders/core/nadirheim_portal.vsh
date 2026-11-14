#version 330

// Portail de Nadirheim : on transmet la position À L'ÉCRAN de chaque sommet, pour que le fragment shader
// accroche le motif à l'écran (et non à la face du bloc) -> effet de "fenêtre" sur un autre espace.

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in vec3 Position;

out vec4 screenPos;
out float sphericalVertexDistance;
out float cylindricalVertexDistance;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    screenPos = gl_Position;
    sphericalVertexDistance = fog_spherical_distance(Position);
    cylindricalVertexDistance = fog_cylindrical_distance(Position);
}
