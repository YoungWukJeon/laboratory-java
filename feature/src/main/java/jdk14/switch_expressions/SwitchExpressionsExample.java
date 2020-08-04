package jdk14.switch_expressions;

public class SwitchExpressionsExample {
    public static void main(String[] args) {
        SwitchExpressionsExample example = new SwitchExpressionsExample();
        System.out.println(example.printDay(Day.FRI));
        System.out.println(example.printDay(Day.TUE));
        System.out.println(example.printDay(Day.SUN));
    }

    public String printDay(Day today) {
       return switch (today) {
            case MON, TUE, WED, THUR, FRI -> today.name() + " is Weekday";
            case SAT, SUN -> {
                System.out.print("Holiday! ");
                yield today.name() + " is Weekend";
            }
        };
    }

    enum Day {
        MON, TUE, WED, THUR, FRI, SAT, SUN
    }
}
