package testApplication.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.security.RolesAllowed;
import org.apache.tomcat.Jar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import testApplication.springbootmongodb.model.TodoDTO;
import testApplication.springbootmongodb.model.UserDTO;
import testApplication.springbootmongodb.repository.TodoRepository;
import testApplication.springbootmongodb.repository.Userrepo;

import java.time.Duration;
import java.util.*;
import java.util.function.DoublePredicate;

@Service
@RestController("/todo")
public class TodoController {

    private TodoRepository todorepo;
    private Userrepo usr;
    private final Bucket bucket=Bucket.builder().addLimit(Bandwidth.classic(10, Refill.intervally(100, Duration.ofMinutes(1)))).build();

    @Autowired
    public TodoController(TodoRepository todorepo,Userrepo repo) {
        this.todorepo = todorepo;
        this.usr=repo;

    }



    @GetMapping("/user/amount")
    public ResponseEntity<?> getAllToodos(){
        if(!bucket.tryConsume(1)){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS.toString(),HttpStatus.TOO_MANY_REQUESTS);
        }
        System.out.println(bucket.getAvailableTokens());
        List<TodoDTO> todos=todorepo.findAll();
        if(todos.isEmpty()){
            System.out.println(bucket.getAvailableTokens());
            return new ResponseEntity<>("no data",HttpStatus.NOT_FOUND); 
        }
        return new ResponseEntity<List<TodoDTO>>(todos,HttpStatus.OK);
    }

    @PostMapping("/admin/postnewtrasction")
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO todo){
        try {
            todo.setDtOfTransDate(new Date(System.currentTimeMillis()));
            todorepo.save(todo);
            return new ResponseEntity<TodoDTO>(todo, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage()+"Not possible",HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/user/aggregate")
    @Cacheable("AgrregateByCache")
    public String getAllagregate(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<String,HashMap<String,Float>> d=new HashMap<>();
        d.put("Credit",new HashMap<String,Float>());
        d.put("Debit",new HashMap<String,Float>());
        for(TodoDTO todo:todos){
            String credordeb=todo.getCreOrDebt();
            Float amt=todo.getAmount();
            String cur_rate=todo.getCur_rate();
            HashMap<String,Float> p=d.get(credordeb);
            if(!p.containsKey(cur_rate)){
                p.put(cur_rate,amt);
            }
            else{
                p.put(cur_rate,p.get(cur_rate)+amt);
            }
            d.put(credordeb,p);
            System.out.println("Parent"+d.toString());
            System.out.println("Sub"+p.toString());
        }
        return d.toString();

    }
    @GetMapping(value = "/user/byweek")
    @Cacheable("WeekByCache")
    public String getallbyweek(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<Integer,HashMap<String,HashMap<String,Float>>>d=new HashMap<>();
        for(int i=1;i<=52;i+=1){
            d.put(i,new HashMap<String,HashMap<String,Float>>());
        }
        for(int i=1;i<=52;i+=1){
            HashMap<String,HashMap<String,Float>> f=d.get(i);
            f.put("Credit",new HashMap<String,Float>());
            f.put("Debit",new HashMap<String,Float>());
        }
        for(TodoDTO todo:todos){
            Date f=todo.getDtOfTransDate();
            Calendar calendar=Calendar.getInstance();
            int i=calendar.get(Calendar.WEEK_OF_YEAR);
            HashMap<String,HashMap<String,Float>> x=d.get(i);
            HashMap<String,Float> y=x.get(todo.getCreOrDebt());
            if(!y.containsKey(todo.getCur_rate())){
                y.put(todo.getCur_rate(), todo.getAmount());
            }
            else{
                y.put(todo.getCur_rate(),y.get(todo.getCur_rate())+ todo.getAmount());
            }
            x.put(todo.getCreOrDebt(),y);
            d.put(i,x);
        }

        return d.toString();
    }

    @GetMapping("/user/aggrebymon")
    @Cacheable("MonthByCache")
    public String getla(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        List <TodoDTO>todos=todorepo.findAll();
        HashMap<Integer,Float> d=new HashMap<Integer,Float>();
        HashMap<Integer,HashMap<String,HashMap<String,Float>>> h=new HashMap<>();
        for(int i=1;i<=12;i+=1){
            d.put(i,0.0f);
            h.put(i,new HashMap<String,HashMap<String,Float>>());
        }
        for(int i=1;i<=12;i+=1){
            HashMap<String,HashMap<String,Float>>x=h.get(i);
            x.put("Credit",new HashMap<String,Float>());
            x.put("Debit",new HashMap<String,Float>());
            h.put(i,x);
        }
        for(TodoDTO todo:todos){
            int y=todo.getDtOfTransDate().getMonth()+1;
            if(todo.getCreOrDebt().equalsIgnoreCase("Credit")){d.put(y,d.get(y)+todo.getAmount());}
            HashMap<String,HashMap<String,Float>> p=h.get(y);
            HashMap<String,Float> q=p.get(todo.getCreOrDebt());
            if(!q.containsKey(todo.getCur_rate())){
                q.put(todo.getCur_rate(),todo.getAmount());
            }
            else{
                q.put(todo.getCur_rate(),q.get(todo.getCur_rate())+todo.getAmount());
            }
            p.put(todo.getCreOrDebt(),q);
            h.put(y,p);


        }
        return h.toString();
    }

//"YearByCache","MonthByCache","WeekByCache","AgrregateByCache"
    @GetMapping("/user/aggrebyyear")
    @Cacheable("YearByCache")
    public String getby(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<Integer,Float> h=new HashMap<Integer,Float>();
        HashMap<Integer,HashMap<String,HashMap<String,Float>>> s=new HashMap<>();

        for(TodoDTO todo:todos){
            int i=todo.getDtOfTransDate().getYear();
            h.put(i+1900,0.0f);
            s.put(i+1900,new HashMap<String,HashMap<String,Float>>());
        }
        for(TodoDTO t:todos){
            int year=t.getDtOfTransDate().getYear()+1900;
            if(t.getCreOrDebt().equals("Credit")) {
                h.put(year,h.get(year)+t.getAmount());
            }
            HashMap<String,HashMap<String,Float>> f=s.get(year);
            f.put("Credit",new HashMap<String,Float>());
            f.put("Debit",new HashMap<String,Float>());
        }
        for(TodoDTO dfs:todos){
            int year=dfs.getDtOfTransDate().getYear()+1900;
            HashMap<String,HashMap<String,Float>> f=s.get(year);
            HashMap<String,Float> f1=f.get(dfs.getCreOrDebt());
            if(!f1.containsKey(dfs.getCur_rate())){
                f1.put(dfs.getCur_rate(), dfs.getAmount());
            }
            else{
                f1.put(dfs.getCur_rate(), f1.get(dfs.getCur_rate())+ dfs.getAmount());
            }
            f.put(dfs.getCreOrDebt(),f1);
            s.put(year,f);

        }
        return s.toString();
    }

    @GetMapping("/user/conversiontoINR")
    @Cacheable("currConvertertoINR")
    public String CurrencyconversionTOINR(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        List<TodoDTO> uss=todorepo.findAll();
        HashMap<String,Double>d=new HashMap<String,Double>();
        d.put("USD",83.333);
        d.put("INR",1.00);
        HashMap<String,Double>x=new HashMap<>();
        x.put("Credit",0.0D);
        x.put("Debit",0.0D);
        for(TodoDTO u:uss){
            String s1=u.getCreOrDebt();
            String s2=u.getCur_rate();
            System.out.println(x);
            x.put(s1,x.get(s1)+u.getAmount()*d.get(s2));

        }
        return x.toString();

    }
    @GetMapping("/user/conversiontoUSD")
    @Cacheable("currConvertertoUSD")
    public String CurrencyconversionTOUSD(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        List<TodoDTO> uss=todorepo.findAll();
        HashMap<String,Double>d=new HashMap<String,Double>();
        d.put("USD",1.00);
        d.put("INR",0.012);
        HashMap<String,Double>x=new HashMap<>();
        x.put("Credit",0.0D);
        x.put("Debit",0.0D);
        for(TodoDTO u:uss){
            String s1=u.getCreOrDebt();
            String s2=u.getCur_rate();
            System.out.println(x);
            x.put(s1,x.get(s1)+u.getAmount()/d.get(s2));

        }
        return x.toString();
    }


    @GetMapping("/user/aggrebyyear/INR")
    @Cacheable("currConvertertoINRInyear")
    public String vonv(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<String,Double> d1=new HashMap<>();
        d1.put("INR",83.33);
        HashMap<Integer,HashMap<String,Double>>h=new HashMap<>();
        for(TodoDTO todo:todos){
            int i=todo.getDtOfTransDate().getYear()+1900;
            h.put(i,new HashMap<String,Double>());
        }
        for(TodoDTO t:todos){
            int year=t.getDtOfTransDate().getYear()+1900;
            HashMap<String,Double> f=h.get(year);
            f.put("Credit",0.0D);
            f.put("Debit",0.0D);
            h.put(year,f);
        }

        for(TodoDTO t:todos){
            int year=t.getDtOfTransDate().getYear()+1900;
            HashMap<String,Double>f=h.get(year);
            String s1=t.getCreOrDebt();
            String s2=t.getCur_rate();
            if(d1.containsKey(s2)){
                f.put(s1,f.get(s1)+(t.getAmount()*d1.get(s2)));
            }
            else{
                f.put(s1,f.get(s1)+t.getAmount());
            }
            h.put(year,f);
        }
        return h.toString();

    }

    @GetMapping("/user/aggrebyyear/USD")
    @Cacheable("currConvertertoINRInyear")
    public String vv(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<String,Double> d1=new HashMap<>();
        d1.put("USD",0.0012);
        HashMap<Integer,HashMap<String,Double>>h=new HashMap<>();
        for(TodoDTO todo:todos){
            int i=todo.getDtOfTransDate().getYear()+1900;
            h.put(i,new HashMap<String,Double>());
        }
        for(TodoDTO t:todos){
            int year=t.getDtOfTransDate().getYear()+1900;
            HashMap<String,Double> f=h.get(year);
            f.put("Credit",0.0D);
            f.put("Debit",0.0D);
            h.put(year,f);
        }

        for(TodoDTO t:todos){
            int year=t.getDtOfTransDate().getYear()+1900;
            HashMap<String,Double>f=h.get(year);
            String s1=t.getCreOrDebt();
            String s2=t.getCur_rate();
            if(d1.containsKey(s2)){
                f.put(s1,f.get(s1)+(t.getAmount()/d1.get(s2)));
            }
            else{
                f.put(s1,f.get(s1)+t.getAmount());
            }
            h.put(year,f);
        }
        return h.toString();

    }
    @GetMapping("/user/aggrebymon/INR")
    @Cacheable("currConverterUSDtoINRInyearBymon")
    public String getbymoninr(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<Integer,HashMap<String,Double>>h=new HashMap<>();
        for(int i=1;i<=12;i+=1){
            h.put(i,new HashMap<String,Double>());
            HashMap<String,Double> x=h.get(i);
            x.put("Credit",0.0D);
            x.put("Debit",0.0D);
        }
        for(TodoDTO todd:todos){
            int m=todd.getDtOfTransDate().getMonth()+1;
            HashMap<String, Double>x=h.get(m);
            String s1=todd.getCreOrDebt();
            String s2=todd.getCur_rate();
            if(s2.equalsIgnoreCase("Inr")){
                x.put(s1,x.get(s1)+todd.getAmount());
            }
            else{
                x.put(s1,x.get(s1)+todd.getAmount()*83.33);
            }
            h.put(m,x);
        }
        return h.toString();

    }

    @GetMapping("/user/aggrebymon/USD")
    @Cacheable("currConverterINRtoUSDInyearBymon")
    public String getbymonusd(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<Integer,HashMap<String,Double>>h=new HashMap<>();
        for(int i=1;i<=12;i+=1){
            h.put(i,new HashMap<String,Double>());
            HashMap<String,Double> x=h.get(i);
            x.put("Credit",0.0D);
            x.put("Debit",0.0D);
        }
        for(TodoDTO todd:todos){
            int m=todd.getDtOfTransDate().getMonth()+1;
            HashMap<String, Double>x=h.get(m);
            String s1=todd.getCreOrDebt();
            String s2=todd.getCur_rate();
            if(s2.equalsIgnoreCase("Inr")){
                x.put(s1,x.get(s1)+todd.getAmount());
            }
            else{
                x.put(s1,x.get(s1)+todd.getAmount()/0.0012);
            }
            h.put(m,x);
        }
        return h.toString();

    }
    @GetMapping("/user/aggrebyweek/INR")
    @Cacheable("currConverterUSDtoINRbyweek")
    public String convbyweekINR(){
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<Integer,HashMap<String,Double>> d=new HashMap<>();
        for(int i=1;i<=52;i+=1){
            d.put(i,new HashMap<String,Double>());
            HashMap<String,Double> x=d.get(i);
            x.put("Credit",0.0D);
            x.put("Debit",0.0D);
            d.put(i,x);

        }
        for(TodoDTO todo:todos){
            Date f=todo.getDtOfTransDate();
            Calendar calendar=Calendar.getInstance();
            int i=calendar.get(Calendar.WEEK_OF_YEAR);
            HashMap<String,Double> h=d.get(i);
            String s1=todo.getCur_rate();
            if(s1.equalsIgnoreCase("inr")){
                h.put(todo.getCreOrDebt(),h.get(todo.getCreOrDebt())+todo.getAmount());

            }
            else{
                h.put(todo.getCreOrDebt(),h.get(todo.getCreOrDebt())+todo.getAmount()*83.33);
            }
            d.put(i,h);
        }
        return d.toString();
    }

    @GetMapping("/user/aggrebyweek/USD")
    @Cacheable("currConverterINRtoUSDbyweek")
    public String convbyweekUSD(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        List<TodoDTO> todos=todorepo.findAll();
        HashMap<Integer,HashMap<String,Double>> d=new HashMap<>();
        for(int i=1;i<=52;i+=1){
            d.put(i,new HashMap<String,Double>());
            HashMap<String,Double> x=d.get(i);
            x.put("Credit",0.0D);
            x.put("Debit",0.0D);
            d.put(i,x);

        }
        for(TodoDTO todo:todos){
            Date f=todo.getDtOfTransDate();
            Calendar calendar=Calendar.getInstance();
            int i=calendar.get(Calendar.WEEK_OF_YEAR);
            HashMap<String,Double> h=d.get(i);
            String s1=todo.getCur_rate();
            if(s1.equalsIgnoreCase("USD")){
                h.put(todo.getCreOrDebt(),h.get(todo.getCreOrDebt())+todo.getAmount());

            }
            else{
                h.put(todo.getCreOrDebt(),h.get(todo.getCreOrDebt())+todo.getAmount()/0.0012);
            }
            d.put(i,h);
        }
        return d.toString();
    }

}



