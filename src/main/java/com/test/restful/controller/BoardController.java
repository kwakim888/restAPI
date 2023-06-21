package com.test.restful.controller;

import com.test.restful.entity.Board;
import com.test.restful.exception.NotFoundException;
import com.test.restful.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @GetMapping("/board")
    public List<Board> findAllBoard() {
        return boardRepository.findAll();
    }

    @GetMapping("/board/{seq}")
    public Resource<Board> findBoardBySeq(@PathVariable int seq) {

        Optional<Board> BoardInfo = boardRepository.findById(seq);

        if (!BoardInfo.isPresent()) {
            throw new NotFoundException(String.format("seq[%s} not found", seq));
        }

        // hateoas : response에 관련 링크를 넣어서 보내준다.
        Resource<Board> resource = new Resource<>(BoardInfo.get());   // board 정보를 resource에 넣어준다.
        ControllerLinkBuilder link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(this.getClass()).findAllBoard());  // findAllBoard 메서드를 연결시켜준다.
        resource.add(link.withRel("find all board"));

        return resource;
    }

    @PostMapping("/board")
    public ResponseEntity<Board> saveBoard(@Valid @RequestBody Board board) {
        Board saveBoard = boardRepository.save(board);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest() // 사용자 요청 uri
                        .path("/{seq}")  // 추가 정보
                        .buildAndExpand(saveBoard.getSeq())   // seq를 uri에 넣음
                        .toUri();   // uri 완성

        // response header에 값을 uri 정보를 전달한다.
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/board/{seq}")
    public ResponseEntity<Board> updateBoard(@PathVariable int seq, @RequestBody Board board) {
        Optional<Board> findBoard = boardRepository.findById(seq);

        if (!findBoard.isPresent()) {
            throw new NotFoundException(String.format("seq{%s} not found", seq));
        }

        Board temp = findBoard.get();
        temp.setTitle(board.getTitle());
        temp.setTitle(board.getTitle());
        temp.setModDate(board.getModDate());

        Board updateBoard = boardRepository.save(temp);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("{seq}")
                        .buildAndExpand(updateBoard.getSeq())
                        .toUri();

        return ResponseEntity.created(location).build();

    }

    @DeleteMapping("/board/{seq}")
    public void deleteBoard(@PathVariable int seq) {
        boardRepository.deleteById(seq);
    }

}
