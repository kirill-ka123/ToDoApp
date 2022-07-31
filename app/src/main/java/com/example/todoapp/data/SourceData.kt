package com.example.todoapp.data

import com.example.todoapp.models.Importance
import com.example.todoapp.models.TodoItem

object SourceData {
    val todoItems = mutableListOf(
        TodoItem(
            "0",
            "Сходить к адвокату",
            Importance.BASIC,
            1678950803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "1",
            "Почистить зубы",
            Importance.IMPORTANT,
            1669950803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "2",
            "Написать огромное сочинение по русскому языку на тему: \"Как я классно провел лето и попал в школу мобильной разработки от Яндекса\"",
            Importance.LOW,
            1699250803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "3",
            "Прогуляться",
            Importance.IMPORTANT,
            1659550803,
            true,
            1658949803,
            1658950203
        ),
        TodoItem(
            "4",
            "Привет",
            Importance.LOW,
            1685950803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "5",
            "Выбрать подарок на день рождения сестре, подготовить празничный стол, разослать приглашения",
            Importance.BASIC,
            1659650803,
            true,
            1658949803,
            1658950203
        ),
        TodoItem(
            "6",
            "Сделать ДЗ",
            Importance.IMPORTANT,
            1659750803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "7",
            "А",
            Importance.LOW,
            1658999803,
            true,
            1658949803,
            1658950203
        ),
        TodoItem(
            "8",
            "Посмотреть лекцию от яндекс школы по теме инструменты разработки",
            Importance.IMPORTANT,
            1678950803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "9",
            "Отдохнуть",
            Importance.BASIC,
            1668950803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "10",
            "Сходить в магазин",
            Importance.IMPORTANT,
            1659950803,
            true,
            1658949803,
            1658950203
        ),
        TodoItem(
            "11",
            "Купить что-то",
            Importance.LOW,
            1659050803,
            false,
            1658949803,
            1658950203
        ),
        TodoItem(
            "12",
            "Попить чаю",
            Importance.BASIC,
            1659550803,
            true,
            1658949803,
            1658950203
        ),
        TodoItem(
            "13",
            "Помочь бабушке",
            Importance.LOW,
            1659850803,
            true,
            1658949803,
            1658950203
        ),
        TodoItem(
            "14",
            "Поиграть в доту",
            Importance.IMPORTANT,
            1659250803,
            false,
            1658949803,
            1658950203
        )
    )
}