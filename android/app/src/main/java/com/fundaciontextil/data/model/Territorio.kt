package com.fundaciontextil.data.model

data class Region(val nombre: String, val comunas: List<String>)

object RegionesDeChile {
    val regiones: List<Region> = listOf(
        Region("Arica y Parinacota", listOf("Arica", "Camarones", "General Lagos", "Putre")),
        Region("Tarapacá", listOf("Alto Hospicio", "Iquique", "Pica", "Pozo Almonte")),
        Region("Antofagasta", listOf("Antofagasta", "Calama", "Mejillones", "Taltal", "Tocopilla")),
        Region("Atacama", listOf("Caldera", "Chañaral", "Copiapó", "Vallenar")),
        Region("Coquimbo", listOf("Coquimbo", "Illapel", "La Serena", "Ovalle", "Vicuña")),
        Region(
            "Valparaíso",
            listOf(
                "Concón", "La Calera", "Limache", "Los Andes", "Quillota", "Quilpué",
                "San Antonio", "San Felipe", "Valparaíso", "Villa Alemana", "Viña del Mar",
            ),
        ),
        Region(
            "Metropolitana de Santiago",
            listOf(
                "Estación Central", "La Florida", "Las Condes", "Maipú", "Ñuñoa", "Peñalolén",
                "Providencia", "Puente Alto", "Quilicura", "Recoleta", "San Bernardo", "Santiago",
            ),
        ),
        Region("O'Higgins", listOf("Pichilemu", "Rancagua", "Rengo", "San Fernando", "Santa Cruz")),
        Region("Maule", listOf("Cauquenes", "Constitución", "Curicó", "Linares", "Talca")),
        Region("Ñuble", listOf("Bulnes", "Chillán", "Chillán Viejo", "San Carlos")),
        Region(
            "Biobío",
            listOf(
                "Chiguayante", "Concepción", "Coronel", "Hualpén", "Los Ángeles",
                "San Pedro de la Paz", "Talcahuano",
            ),
        ),
        Region("La Araucanía", listOf("Angol", "Padre Las Casas", "Pucón", "Temuco", "Villarrica")),
        Region("Los Ríos", listOf("La Unión", "Panguipulli", "Río Bueno", "Valdivia")),
        Region("Los Lagos", listOf("Ancud", "Castro", "Osorno", "Puerto Montt", "Puerto Varas")),
        Region("Aysén", listOf("Aysén", "Chile Chico", "Cochrane", "Coyhaique")),
        Region("Magallanes", listOf("Cabo de Hornos", "Porvenir", "Puerto Natales", "Punta Arenas")),
    )

    fun comunasDe(region: String?): List<String> =
        regiones.firstOrNull { it.nombre == region }?.comunas.orEmpty()
}

val CATEGORIAS_SERVICIO: List<String> = listOf(
    "Venta de segunda mano",
    "Arriendo de textiles",
    "Reparación y arreglos",
    "Supra reciclaje",
    "Confección con retazos",
    "Teñido natural",
    "Talleres y capacitación",
)

const val MAXIMO_CATEGORIAS = 2