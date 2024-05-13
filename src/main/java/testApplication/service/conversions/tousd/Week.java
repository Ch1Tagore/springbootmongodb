package testApplication.service.conversions.tousd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testApplication.service.Imple;
import testApplication.service.Retrieve;
import testApplication.springbootmongodb.model.TodoDTO;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class Week implements Imple {

    private final Retrieve retrieve;

    @Autowired
    public Week(Retrieve retrieve) {
        this.retrieve = retrieve;
    }

    public String giveDeb() {
        List<TodoDTO> uss = retrieve.findAll();
        HashMap<String, Double> d = new HashMap<>();
        d.put("USD", 1.00);
        d.put("INR", 0.012);
        HashMap<String, Double> x = new HashMap<>();
        x.put("Credit", 0.0D);
        x.put("Debit", 0.0D);
        for (TodoDTO u : uss) {
            String s1 = u.getCreOrDebt();
            String s2 = u.getCur_rate();
            System.out.println(x);
            x.put(s1, x.get(s1) + u.getAmount() / d.get(s2));
        }
        return x.toString();
    }

    public String giveW() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<Integer, HashMap<String, Double>> d = new HashMap<>();
        for (int i = 1; i <= 52; i += 1) {
            d.put(i, new HashMap<>());
            HashMap<String, Double> x = d.get(i);
            x.put("Credit", 0.0D);
            x.put("Debit", 0.0D);
            d.put(i, x);
        }
        for (TodoDTO todo : todos) {
            Date f = todo.getDtOfTransDate();
            System.out.println(f.getYear() + 1900 + " " + f.getMonth() + " " + f.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.set(f.getYear() + 1900, f.getMonth(), f.getDate());
            int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
            HashMap<String, Double> h = d.get(weekOfYear);
            String s1 = todo.getCur_rate();
            if (s1.equalsIgnoreCase("USD")) {
                h.put(todo.getCreOrDebt(), h.get(todo.getCreOrDebt()) + todo.getAmount());
            } else {
                h.put(todo.getCreOrDebt(), h.get(todo.getCreOrDebt()) + todo.getAmount() / 0.0012);
            }
            d.put(weekOfYear, h);
        }
        return d.toString();
    }

    public String giveY() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<String, Double> d1 = new HashMap<>();
        d1.put("USD", 0.0012);
        HashMap<Integer, HashMap<String, Double>> h = new HashMap<>();
        for (TodoDTO todo : todos) {
            int year = todo.getDtOfTransDate().getYear() + 1900;
            h.put(year, new HashMap<>());
        }
        for (TodoDTO t : todos) {
            int year = t.getDtOfTransDate().getYear() + 1900;
            HashMap<String, Double> f = h.get(year);
            f.put("Credit", 0.0D);
            f.put("Debit", 0.0D);
            h.put(year, f);
        }

        for (TodoDTO t : todos) {
            int year = t.getDtOfTransDate().getYear() + 1900;
            HashMap<String, Double> f = h.get(year);
            String s1 = t.getCreOrDebt();
            String s2 = t.getCur_rate();
            if (d1.containsKey(s2)) {
                f.put(s1, f.get(s1) + (t.getAmount() / d1.get(s2)));
            } else {
                f.put(s1, f.get(s1) + t.getAmount());
            }
            h.put(year, f);
        }
        return h.toString();
    }

    public String giveM() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<Integer, HashMap<String, Double>> h = new HashMap<>();
        for (int i = 1; i <= 12; i += 1) {
            h.put(i, new HashMap<>());
            HashMap<String, Double> x = h.get(i);
            x.put("Credit", 0.0D);
            x.put("Debit", 0.0D);
        }
        for (TodoDTO todd : todos) {
            int m = todd.getDtOfTransDate().getMonth() + 1;
            HashMap<String, Double> x = h.get(m);
            String s1 = todd.getCreOrDebt();
            String s2 = todd.getCur_rate();
            if (s2.equalsIgnoreCase("Inr")) {
                x.put(s1, x.get(s1) + todd.getAmount());
            } else {
                x.put(s1, x.get(s1) + todd.getAmount() / 0.0012);
            }
            h.put(m, x);
        }
        return h.toString();
    }
}
