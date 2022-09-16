package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @PersistenceContext
    EntityManager entityManager;

    public ResponseEntity<List<Player>> getPlayersList(Map<String, Object> allParams) {
        String order = "id";
        if (allParams.containsKey("order")) {
            order = allParams.get("order").toString();
        }
        int pageNumber = 0;
        if (allParams.containsKey("pageNumber")) {
            pageNumber = Integer.parseInt(allParams.get("pageNumber").toString());
        }
        int pageSize = 3;
        if (allParams.containsKey("pageSize")) {
            pageSize = Integer.parseInt(allParams.get("pageSize").toString());
        }
        String text = "SELECT p FROM player p";
        text = text + getWhere(allParams);
        Query query = entityManager.createQuery(text);
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        List <Player> response = query.getResultList();
        return ResponseEntity.ok(response);
    }

    private String getWhere(Map<String, Object> allParams) {
        List<String> wheres = new ArrayList<>();
        if (allParams.containsKey("name")) {
            wheres.add("name like '%" + allParams.get("name") + "%'");
        }
        if (allParams.containsKey("title")) {
            wheres.add("title like '%" + allParams.get("title") + "%'");
        }
        if (allParams.containsKey("race")) {
            wheres.add("race = '" + allParams.get("race") + "'");
        }
        if (allParams.containsKey("profession")) {
            wheres.add("profession = '" + allParams.get("profession") + "'");
        }
        if (allParams.containsKey("after")) {
            Date date = new Date(Long.parseLong(allParams.get("after").toString()));
            String text = new SimpleDateFormat("yyyy-MM-dd").format(date);
            wheres.add("birthday >= '" + text + "'");
        }
        if (allParams.containsKey("before")) {
            Date date = new Date(Long.parseLong(allParams.get("before").toString()));
            String text = new SimpleDateFormat("yyyy-MM-dd").format(date);
            wheres.add("birthday <= '" + text + "'");
        }
        if (allParams.containsKey("banned")) {
            int flag = 0;
            if (allParams.get("banned").toString().equalsIgnoreCase("true")) {
                flag = 1;
            }
            wheres.add("banned = " + flag);
        }
        if (allParams.containsKey("minExperience")) {
            wheres.add("experience >= " + Integer.parseInt(allParams.get("minExperience").toString()));
        }
        if (allParams.containsKey("maxExperience")) {
            wheres.add("experience <= " + Integer.parseInt(allParams.get("maxExperience").toString()));
        }
        if (allParams.containsKey("minLevel")) {
            wheres.add("level >= " + Integer.parseInt(allParams.get("minLevel").toString()));
        }
        if (allParams.containsKey("maxLevel")) {
            wheres.add("level <= " + Integer.parseInt(allParams.get("maxLevel").toString()));
        }
        if (wheres.isEmpty()) {
            return "";
        } else {
            return " where " + String.join(" and ", wheres);
        }
    }

    public long getPlayersCount(Map<String, Object> allParams) {
        String text = "SELECT count(p) FROM player p";
        text = text + getWhere(allParams);
        Query query = entityManager.createQuery(text);
        return (long) query.getSingleResult();
    }

    public ResponseEntity<Player> createPlayer(Player request) {
        if (request.getBirthday() == null ||
                request.getExperience() == null ||
                request.getName() == null ||
                request.getRace() == null ||
                request.getTitle() == null ||
                request.getProfession() == null ||
                request.getName().length() > 12 ||
                request.getTitle().length() > 30 ||
                request.getName().equals("") ||
                request.getExperience() < 0 ||
                request.getExperience() > 10_000_000 ||
                request.getBirthday().getTime() < 0 ||
                request.getBirthday().getYear() < 100 ||
                request.getBirthday().getYear() > 1100
        ) {
            return ResponseEntity.status(400).build();
        }
        Player response = playerRepository.saveAndFlush(new Player(request));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Player> getPlayer(String ID) {
        try {
            long longId = Long.parseLong(ID);
            if (longId < 1) {
                return ResponseEntity.status(400).build();
            }
            Optional<Player> response = playerRepository.findById(longId);
            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).build();
        }
    }

    public ResponseEntity<Player> updatePlayer(String id, Player request) {
        long longId;
        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).build();
        }
        if (longId < 1 ||
                (request.getName() != null && request.getName().length() > 12) ||
                (request.getTitle() != null && request.getTitle().length() > 30) ||
                (request.getName() != null && request.getName().equals("")) ||
                (request.getExperience() != null && request.getExperience() < 0) ||
                (request.getExperience() != null && request.getExperience() > 10_000_000) ||
                (request.getBirthday() != null && request.getBirthday().getTime() < 0) ||
                (request.getBirthday() != null && request.getBirthday().getYear() < 100) ||
                (request.getBirthday() != null && request.getBirthday().getYear() > 1100)
        ) {
            return ResponseEntity.status(400).build();
        }
        Optional<Player> response = playerRepository.findById(longId);
        if (!response.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Player player = response.get();
        if (request.getName() != null) {
            player.setName(request.getName());
        }
        if (request.getTitle() != null) {
            player.setTitle(request.getTitle());
        }
        if (request.getRace() != null) {
            player.setRace(request.getRace());
        }
        if (request.getProfession() != null) {
            player.setProfession(request.getProfession());
        }
        if (request.getBirthday() != null) {
            player.setBirthday(request.getBirthday());
        }
        if (request.getBanned() != null) {
            player.setBanned(request.getBanned());
        }
        if (request.getExperience() != null) {
            player.setExperience(request.getExperience());
            player.setLevel((int) ((Math.sqrt(player.getExperience() * 200 + 2500) - 50) / 100));
            player.setUntilNextLevel((player.getLevel() + 1) * (player.getLevel() + 2) * 50 - player.getExperience());
        }
        return ResponseEntity.ok(playerRepository.saveAndFlush(player));
    }

    public ResponseEntity deletePlayer(String id) {
        long longId;
        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).build();
        }
        if (longId < 1) {
            return ResponseEntity.status(400).build();
        }
        Optional<Player> response = playerRepository.findById(longId);
        if (response.isPresent()) {
            playerRepository.delete(response.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
