package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        Map<LocalDateTime, Integer> calories = new HashMap<LocalDateTime, Integer>();
        for (UserMeal um : meals) {
            LocalDateTime startOfDay = um.getDateTime().with(LocalTime.MIN);
            calories.merge(startOfDay, um.getCalories(), (prev, one) -> prev + one);
        }

        List<UserMealWithExcess> mealsTo = new ArrayList<UserMealWithExcess>();
        for (UserMeal um : meals) {
            if (TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime)) {
                LocalDateTime startOfDay = um.getDateTime().with(LocalTime.MIN);
                boolean excess = (calories.get(startOfDay) <= caloriesPerDay);
                mealsTo.add(new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(), excess));
            }
        }

        return mealsTo;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams

        Map<LocalDateTime, Integer> calories = meals.stream()
                .collect(Collectors.toMap(
                        um -> um.getDateTime().with(LocalTime.MIN),
                        um -> um.getCalories(),
                        (prev, tek) -> prev + tek
                        )
                );

        return meals.stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> {
                            LocalDateTime startOfDay = um.getDateTime().with(LocalTime.MIN);
                            boolean excess = (calories.get(startOfDay) <= caloriesPerDay);
                            return new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(), excess);
                        }
                ).collect(Collectors.toList());
    }


}
