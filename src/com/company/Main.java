package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    final static String ROOT_DIR = "C://games//savegames//";
    final static String FILE_END = ".dat";
    static StringBuilder LOG;
    // сохранить состояние объекта в файл
    static void saveProgress(GameProgress save, String name) {
        // потоки
        try(FileOutputStream fos = new FileOutputStream(name);
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // сохранение
            oos.writeObject(save);
        } catch (IOException exp) {
            LOG.append("\nОшибка сохранения файла ").append(name).append(" :").append(exp.getMessage());
        }
    }
    // Архивация
    static void zipFiles(String path, ArrayList<String> files) {
        // архивация
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path + "//saves.zip"))) {
            // цикл по файлам
            for(String file : files) {
                // текущий файл
                File curFile = new File(file);
                // поток данных файла
                FileInputStream fis = new FileInputStream(curFile);
                // вхождение файла в архив
                ZipEntry entry = new ZipEntry(curFile.getName());
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
            for(String rec : files) {
                File file = new File(rec);
                // удаляем
                if(!file.delete()) LOG.append("\nНе удалось удалить файл: ").append(file.getName());
            }
        } catch (IOException exp) {
            LOG.append("\nОшибка архивирования файлов: ").append(exp.getMessage());
        }
    }

    public static void main(String[] args) {
        boolean dirExist;
        String fileName;
        ArrayList<String> filesToZip = new ArrayList<>();
        GameProgress[] saves = {new GameProgress(100, 100, 1, 1),
                                new GameProgress(90, 90, 2, 2),
                                new GameProgress(80, 80, 3, 3)};
        LOG = new StringBuilder();
        // рабдочая директория
        File wrkDir = new File(ROOT_DIR);
        dirExist = wrkDir.exists();
        // если нет, то пробуем создать
        if(!dirExist) dirExist = wrkDir.mkdir();
        // есть директория
        if(dirExist) {
            // цикл по объектам
            for(int i = 0; i < saves.length; i++) {
                fileName = wrkDir + "//save" + i + FILE_END;
                // сохранение в файл состояний объектов
                saveProgress(saves[i], fileName);
                filesToZip.add(fileName);
            }
            // Архивация
            zipFiles(wrkDir.getPath(), filesToZip);
        } else LOG.append("\nДиректории ").append(ROOT_DIR).append(" не существует, сохранение невозможно!");
        if(!LOG.isEmpty()) System.out.println(LOG.toString());
    }
}
