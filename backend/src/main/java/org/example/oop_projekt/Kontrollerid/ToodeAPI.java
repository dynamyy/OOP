package org.example.oop_projekt.Kontrollerid;


import org.example.oop_projekt.andmepääsukiht.Toode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/tooted")
public class ToodeAPI {

    private Toode toode;

    /*

    @Autowired
    public ToodeAPI(ToodeService toodeService){
        this.toodeService = toodeService;
    }

    @GetMapping
    public List<Toode> getToode(){
        return toodeService.getToode():
    }


     */


}
