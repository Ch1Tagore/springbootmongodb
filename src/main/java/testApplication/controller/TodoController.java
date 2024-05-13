package testApplication.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import testApplication.service.FindByYearAndMonthAndDay;
import testApplication.service.Retrieve;
import testApplication.service.conversions.toinr.Weekly;
import testApplication.service.conversions.tousd.Week;
import testApplication.springbootmongodb.model.TodoDTO;
import testApplication.springbootmongodb.model.UserDTO;
import testApplication.springbootmongodb.repository.TodoRepository;
import testApplication.springbootmongodb.repository.Userrepo;

import java.time.Duration;
import java.util.*;

@Service
@RestController("/todo")
public class TodoController {

    private Retrieve retrieve;
    private FindByYearAndMonthAndDay find;

    private Weekly weekly;

    private Week week;
    private final Bucket bucket=Bucket.builder().addLimit(Bandwidth.classic(10, Refill.intervally(100, Duration.ofMinutes(1)))).build();

    public TodoController(Retrieve retrieve, FindByYearAndMonthAndDay find,Weekly weekly,Week week) {
        this.retrieve = retrieve;
        this.find = find;
        this.weekly=weekly;
        this.week=week;
    }

    @GetMapping("/amount")
    public ResponseEntity<?> getAllToodos(){
        if(!bucket.tryConsume(1)){
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS.toString(),HttpStatus.TOO_MANY_REQUESTS);
        }
        return new ResponseEntity<List<TodoDTO>>(retrieve.findAll(),HttpStatus.OK);
    }

    @PostMapping("/postnewtransaction")
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO todo){
        try {
            todo.setDtOfTransDate(new Date(System.currentTimeMillis()));
            retrieve.save(todo);
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
        return find.getAll();

    }
    @GetMapping(value = "/byweek")
    @Cacheable("WeekByCache")
    public String getallbyweek(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return find.getAllByWeek();
    }

    @GetMapping("/aggryebymon")
    @Cacheable("MonthByCache")
    public String getla(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return find.getAllByMonth();
    }

//"YearByCache","MonthByCache","WeekByCache","AgrregateByCache"
    @GetMapping("/aggrebyyear")
    @Cacheable("YearByCache")
    public String getby(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return find.getAllByYear();
    }

    @GetMapping("/conversiontoINR")
    @Cacheable("currConvertertoINR")
    public String CurrencyconversionTOINR(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        System.out.println(bucket.getAvailableTokens());
        return weekly.giveY();
    }
    @GetMapping("/conversiontoUSD")
    @Cacheable("currConvertertoUSD")
    public String CurrencyconversionTOUSD(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }

        System.out.println(bucket.getAvailableTokens());
        return week.giveDeb();
    }


    @GetMapping("/user/aggrebyyear/INR")
    @Cacheable("currConvertertoINRInyear")
    public String vonv(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return weekly.giveDeb();
    }

    @GetMapping("/aggrebyyear/USD")
    @Cacheable("currConvertertoINRInyear")
    public String vv(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return week.giveY();

    }
    @GetMapping("/aggrebymon/INR")
    @Cacheable("currConverterUSDtoINRInyearBymon")
    public String getbymoninr(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return weekly.giveM();
    }

    @GetMapping("/aggrebymon/USD")
    @Cacheable("currConverterINRtoUSDInyearBymon")
    public String getbymonusd(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return week.giveM();

    }
    @GetMapping("/aggrebyweek/INR")
    @Cacheable("currConverterUSDtoINRbyweek")
    public String convbyweekINR(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return weekly.giveW();
    }

    @GetMapping("/aggrebyweek/USD")
    @Cacheable("currConverterINRtoUSDbyweek")
    public String convbyweekUSD(){
        if(!bucket.tryConsume(1)){
            return HttpStatus.TOO_MANY_REQUESTS.toString();
        }
        System.out.println(bucket.getAvailableTokens());
        return week.giveW();
    }

}



