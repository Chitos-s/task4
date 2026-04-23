RAMApp

ФИО: Бородина Полина Андреевна  
Группа: Б9123-09.03.03пикд

Используемый API:  
Rick and Morty API: https://rickandmortyapi.com/documentation

Endpoints:
1. `GET /character?page={page}&name={name}&status={status}&species={species}&type={type}&gender={gender}`
2. `GET /character/{id}`

Что хранится в Room:
1. `stored_characters`
   - Кэш последнего успешного результата поиска по выбранному фильтру и страницам.
   - Используется для восстановления списка после перезапуска и как fallback при ошибке сети.
   - Detail тоже может открываться из Room без API, если персонаж уже был сохранён.
2. `filter_state`
   - Последний выбранный фильтр, текущая страница и `totalPages` из последнего успешного ответа API.

Как проверить:
1. Выставить фильтры, например `status=alive`, `gender=female`, и загрузить список.
2. Перейти на другую страницу списка.
3. Полностью закрыть приложение.
4. Выключить интернет.
5. Открыть приложение снова.
6. Должны восстановиться последний фильтр, текущая страница и данные из Room.
7. Открыть detail уже сохранённого персонажа.
8. Detail должен открыться из Room без нового сетевого запроса.

Скриншоты:
1. `screenshots/List.jpg` - экран списка с фильтрами
2. `screenshots/Loading.jpg` - состояние загрузки
3. `screenshots/Error.jpg` - состояние ошибки
4. `screenshots/Detail.jpg` - экран detail
5. `screenshots/RestoredAfterRestart.jpg` - восстановление после перезапуска
6. `screenshots/Room.jpg` - данные Room в Database Inspector
