package com.example.cityguide

data class Place(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val imageResId: Int
)

object PlacesRepository {
    val places = listOf(
        Place(
            id = "lenin_square",
            title = "Площадь Ленина",
            category = "Центр города",
            description = "Главная площадь Хабаровска с фонтанами, прогулочными зонами и городскими мероприятиями.",
            latitude = 48.480208,
            longitude = 135.071964,
            imageResId = R.drawable.place_lenin_square
        ),
        Place(
            id = "amur_embankment",
            title = "Набережная Амура",
            category = "Прогулки",
            description = "Популярное место для прогулок с видом на Амур, речной вокзал и городские закаты.",
            latitude = 48.471156,
            longitude = 135.053526,
            imageResId = R.drawable.place_amur_embankment
        ),
        Place(
            id = "utes",
            title = "Утёс",
            category = "Смотровая точка",
            description = "Историческая смотровая площадка над Амуром и один из узнаваемых символов города.",
            latitude = 48.472617,
            longitude = 135.049685,
            imageResId = R.drawable.place_utes
        ),
        Place(
            id = "cathedral",
            title = "Спасо-Преображенский собор",
            category = "Архитектура",
            description = "Крупный православный собор на высоком берегу Амура рядом с Комсомольской площадью.",
            latitude = 48.466025,
            longitude = 135.067211,
            imageResId = R.drawable.place_cathedral
        ),
        Place(
            id = "grodekov_museum",
            title = "Хабаровский краевой музей имени Н. И. Гродекова",
            category = "Музей",
            description = "Краеведческий музей с экспозициями об истории, природе и культуре Дальнего Востока.",
            latitude = 48.473242,
            longitude = 135.050844,
            imageResId = R.drawable.place_grodekov_museum
        ),
        Place(
            id = "dynamo_park",
            title = "Парк \"Динамо\"",
            category = "Парк",
            description = "Зелёная зона в центре города для прогулок, отдыха и сезонных городских событий.",
            latitude = 48.48131,
            longitude = 135.07879,
            imageResId = R.drawable.place_dynamo_park
        ),
        Place(
            id = "amur_bridge",
            title = "Амурский мост",
            category = "Инженерный объект",
            description = "Мост через Амур, важный транспортный объект и часть истории Транссиба.",
            latitude = 48.53547,
            longitude = 134.99584,
            imageResId = R.drawable.place_amur_bridge
        ),
        Place(
            id = "platinum_arena",
            title = "Платинум Арена",
            category = "Спорт и события",
            description = "Ледовая арена и площадка для спортивных матчей, концертов и городских мероприятий.",
            latitude = 48.48439,
            longitude = 135.08675,
            imageResId = R.drawable.place_platinum_arena
        )
    )

    fun findById(id: String): Place = places.firstOrNull { it.id == id } ?: places.first()
}
