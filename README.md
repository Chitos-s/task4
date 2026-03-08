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
	- Хранение списка персонажей по последнему выбранному фильтру и страницам для восстановления после перезапуска/оффлайна, включая открытие detail из Room без API.
2. `filter_state`
	- Хранение последнего выбранного фильтра, текущей страницы и `totalPages` из последнего ответа API.

Как проверить (короткий сценарий):
1. Выставить фильтры (например, `status=alive`, `gender=female`) и загрузить список.
2. Перейти на несколько страниц (или дождаться фоновой докачки страниц).
3. Полностью закрыть приложение.
4. Выключить интернет.
5. Открыть приложение.
6. Восстановился последний фильтр, страница и данные из Room без сброса в дефолт значение при запуске.
7. Открыть detail любого уже сохранённого персонажа.
8. Detail берет данные из бд из Room.

Скриншоты (4–6 шт):
1. `screenshots/List.jpg` — экран списка (Grid) с фильтрами.
2. `screenshots/Loading.jpg` — состояние загрузки.
3. `screenshots/Error.jpg` — состояние ошибки.
4. `screenshots/Detail.jpg` — экран detail персонажа.
5. `screenshots/RestoredAfterRestart.jpg` — восстановление состояния после перезапуска.
6. `screenshots/Room.jpg` — состояние Room (`filter_state` + `stored_characters`) в Database Inspector.
 