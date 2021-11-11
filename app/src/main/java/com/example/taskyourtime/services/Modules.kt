package com.example.taskyourtime.services

import com.example.taskyourtime.model.ToDoItem
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module{
    single { UserServiceImpl( androidContext()) as UserService }
    single {NoteServiceImpl(androidContext()) as NoteService}
    single { CalendarServiceImpl(androidContext()) as CalendarService }
    single {ToDoItemServiceImpl(androidContext()) as ToDoItemService}
}