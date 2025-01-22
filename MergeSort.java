package org.example;

public class MergeSort {

    // Метод для сортировки массива
    public static void mergeSort(int[] array) {
        if (array.length < 2) {
            return; // Массив уже отсортирован
        }
        if (isSorted(array)){
            return;
        }
            int mid = array.length / 2; // Находим середину массива
        int[] left = new int[mid]; // Создаем левую половину
        int[] right = new int[array.length - mid]; // Создаем правую половину

        // Заполняем левую и правую половины
        for (int i = 0; i < mid; i++) {
            left[i] = array[i];
        }
        for (int i = mid; i < array.length; i++) {
            right[i - mid] = array[i];
        }
        // Проверка на отсортированность подмассивов
        if (left.length<=15) {
            insertionSort(left);
            // Если левая половина малая, просто сортируем её более экономично
            System.arraycopy(left, 0, array, 0, left.length);
        } else {
            // Рекурсивно сортируем левую половину
            insertionSort(left);
        }
        if (right.length<=15) {
            mergeSort(right);
            // Если правая половина малая, просто сортируем её более экономично
            System.arraycopy(right, 0, array, mid, right.length);
        } else {
            // Рекурсивно сортируем правую половину
            mergeSort(right);
        }
        // Сливаем отсортированные половины
        merge(array, left, right);
    }
    // Метод для слияния двух отсортированных массивов
    public static void merge(int[] array, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        // Сравниваем элементы и сливаем их в исходный массив
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                array[k++] = left[i++];
            } else {
                array[k++] = right[j++];
            }
        }
        // Копируем оставшиеся элементы левой половины, если есть
        while (i < left.length) {
            array[k++] = left[i++];
        }
        // Копируем оставшиеся элементы правой половины, если есть
        while (j < right.length) {
            array[k++] = right[j++];
        }
    }
    // Метод для сортировки вставками маленьких массивов 10-20 элементов
    public static void insertionSort(int[] array) {
        int n = array.length;
        // Проходим по всем элементам массива, начиная со второго
        for (int i = 1; i < n; i++) {
            int key = array[i]; // Текущий элемент для вставки
            int j = i - 1;
            // Сдвигаем элементы массива, которые больше ключа, на одну позицию вправо
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            // Вставляем ключ на правильную позицию
            array[j + 1] = key;
        }
    }
    // Метод для проверки, отсортирован ли массив
    public static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i - 1]) {
                return false; // Если найден элемент, который меньше предыдущего, массив не отсортирован
            }
        }
        return true; // Массив отсортирован
    }
    // Пример использования
    public static void main(String[] args) {
        int[] array = {38, 27, 43, 3, 9, 82, 10}; // Пример не отсортированного массива
        System.out.println("Исходный массив:");
        printArray(array);

        mergeSort(array);

        System.out.println("Отсортированный массив:");
        printArray(array);
    }
    // Метод для вывода массива
    public static void printArray(int[] array) {
        for (int value : array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
