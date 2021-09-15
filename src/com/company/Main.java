package com.company;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    final static String ROOT_DIR = "C://games//savegames//";
    final static String FILE_END = ".dat";
    // сохранить состояние объекта в файл
    static void saveProgress(GameProgress save, String name) {
        // потоки
        try(FileOutputStream fos = new FileOutputStream(ROOT_DIR + name);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // сохранение
            oos.writeObject(save);
        } catch (IOException exp) {
            System.out.println("Ошибка сохранения файла " + ROOT_DIR + name + " :" + exp.getMessage());
        }
    }
    // Архивация
    static void zipFiles(File dir) {
        // передана директория
        if(dir.isDirectory()) {
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(ROOT_DIR + "saves.zip"))) {
                // цикл по файлам рабочей директории
                for (File file : dir.listFiles((a, b) -> b.endsWith(FILE_END))) {
                    // поток данных файла
                    FileInputStream fis = new FileInputStream(file);
                    // вхождение файла в архив
                    ZipEntry entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);
                    // чтение-запись данных файла
                    byte[] buffer = new byte[fis.available()];
                    if (fis.read(buffer) != -1) zos.write(buffer);
                    // закрытие вхождения
                    zos.closeEntry();
                    // закрытие потока
                    fis.close();
                }
                // удаление файлов вне архива, после архивации
                for (File file : dir.listFiles()) {
                    // имя файла
                    String name = file.getName();
                    // если не архив
                    if (!name.endsWith(".zip")) {
                        // удаляем
                        if(!file.delete()) System.out.println("Не удалось удалить файл: " + file.getName());
                    }
                }
            } catch (IOException exp) {
                System.out.println("Ошибка архивирования файлов: " + exp.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        boolean dirExist;
        GameProgress[] saves = {new GameProgress(100, 100, 1, 1),
                                new GameProgress(90, 90, 2, 2),
                                new GameProgress(80, 80, 3, 3)};
        // рабдочая директория
        File wrkDir = new File(ROOT_DIR);
        dirExist = wrkDir.exists();
        // если нет, то пробуем создать
        if(!dirExist) dirExist = wrkDir.mkdir();
        // есть директория
        if(dirExist) {
            // цикл по объектам
            for(int i = 0; i < saves.length; i++) {
                // сохранение в файл состояний объектов
                saveProgress(saves[i], "save"+i+".dat");
            }
            // Архивация
            zipFiles(wrkDir);
        } else
            System.out.println("Директории " + ROOT_DIR + " не существует, сохранение невозможно!");
    }
}
