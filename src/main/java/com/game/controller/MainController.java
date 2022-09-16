package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/players")
public class MainController {

    private final PlayerService playerService;

    public MainController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("")
    public ResponseEntity<List<Player>> getPlayersList(@RequestParam Map<String,Object> allParams){
        return playerService.getPlayersList(allParams);
    }

    @GetMapping("/count")
    public long getPlayersCount(@RequestParam Map<String,Object> allParams){
        return playerService.getPlayersCount(allParams);
    }

    @PostMapping("")
    public ResponseEntity<Player> createPlayer(@RequestBody Player request){
        return playerService.createPlayer(request);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<Player> getPlayer(@PathVariable String ID) {
        return playerService.getPlayer(ID);
    }

    @PostMapping("/{ID}")
    public ResponseEntity<Player> updatePlayer(@PathVariable String ID, @RequestBody Player request) {
        return playerService.updatePlayer(ID, request);
    }

    @DeleteMapping("/{ID}")
    public ResponseEntity deletePlayer(@PathVariable String ID) {
        return playerService.deletePlayer(ID);
    }
}
