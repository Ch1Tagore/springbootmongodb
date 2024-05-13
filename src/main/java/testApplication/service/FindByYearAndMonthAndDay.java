package testApplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testApplication.springbootmongodb.model.TodoDTO;

import java.util.HashMap;
import java.util.List;

@Service
public class FindByYearAndMonthAndDay {
    private final Retrieve retrieve;

    @Autowired
    public FindByYearAndMonthAndDay(Retrieve retrieve) {
        this.retrieve = retrieve;
    }

    public String getAll() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<String, HashMap<String, Float>> d = new HashMap<>();
        d.put("Credit", new HashMap<>());
        d.put("Debit", new HashMap<>());
        for (TodoDTO todo : todos) {
            String credordeb = todo.getCreOrDebt();
            Float amt = todo.getAmount();
            String cur_rate = todo.getCur_rate();
            HashMap<String, Float> p = d.get(credordeb);
            p.put(cur_rate, p.getOrDefault(cur_rate, 0.0f) + amt);
            d.put(credordeb, p);
        }
        return d.toString();
    }

    public String getAllByWeek() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<String, HashMap<String, Float>> d = new HashMap<>();
        d.put("Credit", new HashMap<>());
        d.put("Debit", new HashMap<>());
        for (TodoDTO todo : todos) {
            String credordeb = todo.getCreOrDebt();
            Float amt = todo.getAmount();
            String cur_rate = todo.getCur_rate();
            HashMap<String, Float> p = d.get(credordeb);
            p.put(cur_rate, p.getOrDefault(cur_rate, 0.0f) + amt);
            d.put(credordeb, p);
        }
        return d.toString();
    }

    public String getAllByMonth() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<Integer, HashMap<String, HashMap<String, Float>>> h = new HashMap<>();
        for (int i = 1; i <= 12; i += 1) {
            h.put(i, new HashMap<>());
            h.get(i).put("Credit", new HashMap<>());
            h.get(i).put("Debit", new HashMap<>());
        }
        for (TodoDTO todo : todos) {
            int y = todo.getDtOfTransDate().getMonth() + 1;
            String credordeb = todo.getCreOrDebt();
            Float amt = todo.getAmount();
            String cur_rate = todo.getCur_rate();
            HashMap<String, HashMap<String, Float>> p = h.get(y);
            HashMap<String, Float> q = p.get(credordeb);
            q.put(cur_rate, q.getOrDefault(cur_rate, 0.0f) + amt);
            p.put(credordeb, q);
            h.put(y, p);
        }
        return h.toString();
    }

    public String getAllByYear() {
        List<TodoDTO> todos = retrieve.findAll();
        HashMap<Integer, HashMap<String, HashMap<String, Float>>> h = new HashMap<>();
        for (TodoDTO todo : todos) {
            int year = todo.getDtOfTransDate().getYear() + 1900;
            h.putIfAbsent(year, new HashMap<>());
            h.get(year).put("Credit", new HashMap<>());
            h.get(year).put("Debit", new HashMap<>());
        }
        for (TodoDTO t : todos) {
            int year = t.getDtOfTransDate().getYear() + 1900;
            String credordeb = t.getCreOrDebt();
            Float amt = t.getAmount();
            String cur_rate = t.getCur_rate();
            HashMap<String, HashMap<String, Float>> f = h.get(year);
            HashMap<String, Float> f1 = f.get(credordeb);
            f1.put(cur_rate, f1.getOrDefault(cur_rate, 0.0f) + amt);
            f.put(credordeb, f1);
            h.put(year, f);
        }
        return h.toString();
    }
}
