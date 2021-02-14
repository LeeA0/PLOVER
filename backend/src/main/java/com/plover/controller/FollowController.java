package com.plover.controller;

import com.plover.exceptions.EntityNotFoundException;
import com.plover.model.Response;
import com.plover.model.follow.request.FollowRequest;
import com.plover.model.follow.response.FollowUsersResponse;
import com.plover.model.user.UserDto;
import com.plover.service.FollowService;
import com.plover.service.UserService;
import com.plover.utils.CookieUtil;
import com.plover.utils.JwtUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ApiResponses(value = {
        @ApiResponse(code = 401, message = "Unauthorized", response = Response.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Response.class),
        @ApiResponse(code = 404, message = "Not Found", response = Response.class),
        @ApiResponse(code = 500, message = "Failure", response = Response.class)})

@RestController
@RequestMapping("follow")
public class FollowController {
    private FollowService followService;
    private UserService userService;
    private JwtUtil jwtUtil;
    private CookieUtil cookieUtil;

    public FollowController(FollowService followService, UserService userService, JwtUtil jwtUtil, CookieUtil cookieUtil) {
        this.followService = followService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
    }

    @GetMapping("/following/{toUserNo}")
    @ApiOperation(value = "로그인한 사용자가 프로필사용자를 팔로우하고있는지 확인 (로그인 필수)",
            notes = "로그인한 사용자의 no와 프로필사용자의 no를 전달받아 현재 팔로우 중인지 확인",
            response = Response.class)
    public Object checkFollowing(HttpServletRequest request, @PathVariable @NotNull Long toUserNo) {
        ResponseEntity response = null;
        try {
            Long fromUserNo = (long) jwtUtil.getNo(cookieUtil.getCookie(request, JwtUtil.ACCESS_TOKEN_NAME).getValue());
            if (fromUserNo != null) {
                Boolean isFollowing = followService.existsFollowing(fromUserNo, toUserNo);
                final Response result = new Response("success", "팔로잉 여부 조회에 성공했습니다.", isFollowing);
                response = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                final Response result = new Response("error", "유효하지 않은 토큰 값입니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            final Response result = new Response("error", "팔로잉 여부 조회에 실패했습니다.", null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping("/follower/{toUserNo}")
    @ApiOperation(value = "프로필사용자가 로그인한 사용자를 팔로우하고있는지 확인 (로그인 필수)",
            notes = "로그인한 사용자의 no와 프로필사용자의 no를 전달받아 현재 팔로우 중인지 확인",
            response = Response.class)
    public Object checkFollower(HttpServletRequest request, @PathVariable @NotNull Long toUserNo) {
        ResponseEntity response = null;
        try {
            Long fromUserNo = (long) jwtUtil.getNo(cookieUtil.getCookie(request, JwtUtil.ACCESS_TOKEN_NAME).getValue());
            if (fromUserNo != null) {
                Boolean isFollowing = followService.existsFollower(fromUserNo, toUserNo);
                final Response result = new Response("success", "팔로워 여부 조회에 성공했습니다.", isFollowing);
                response = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                final Response result = new Response("error", "유효하지 않은 토큰 값입니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            final Response result = new Response("error", "팔로워 여부 조회에 실패했습니다.", null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping("/following/{no}/{cursorId}")
    @ApiOperation(value = "팔로잉 목록 조회",
            notes = "마지막 팔로잉 id를 받아(맨처음에는 0) 팔로잉 목록을 조회한다.",
            response = Response.class)
    public Object getFollowing(@PathVariable @NotNull Long no, @PathVariable @NotNull Long cursorId) {
        ResponseEntity response = null;
        try {
            //TODO:userService에 findUserByNo 추가 후 no관련 예외처리 추가
            FollowUsersResponse followUsersResponse = followService.getFollowingUsers(cursorId, no);
            if (followUsersResponse != null) {
                final Response result = new Response("success", "팔로잉 목록 조회에 성공하였습니다.", followUsersResponse);
                response = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                final Response result = new Response("success", "조회결과가 없습니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            final Response result = new Response("error", "팔로잉 목록 조회에 실패했습니다.", null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping("/follower/{no}/{cursorId}")
    @ApiOperation(value = "팔로워 목록 조회",
            notes = "마지막 팔로워 id를 받아(맨처음에는 0) 팔로워 목록을 조회한다.",
            response = Response.class)
    public Object getFollower(@PathVariable @NotNull Long no, @PathVariable @NotNull Long cursorId) {
        ResponseEntity response = null;
        try {
            //TODO:userService에 findUserByNo 추가 후 no관련 예외처리 추가
            FollowUsersResponse followUsersResponse = followService.getFollowerUsers(cursorId, no);
            if (followUsersResponse != null) {
                final Response result = new Response("success", "팔로워 목록 조회에 성공하였습니다.", followUsersResponse);
                response = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                final Response result = new Response("success", "조회결과가 없습니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            final Response result = new Response("error", "팔로워 목록 조회에 실패했습니다.", null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @PostMapping
    @ApiOperation(value = "팔로잉/팔로워를 저장(request에서 email만 입력하면됨)",
            notes = "로그인한 사용자의 email과 팔로우한 사용자의 email을 전달받아 팔로잉/팔로워를 저장한다. (로그인 필수)",
            response = Response.class)
    public Object saveFollow(HttpServletRequest request, @Valid @RequestBody FollowRequest followRequest) {
        ResponseEntity response = null;
        try {
            String fromUserEmail = jwtUtil.getEmail(cookieUtil.getCookie(request, JwtUtil.ACCESS_TOKEN_NAME).getValue());
            if (fromUserEmail != null) {
                //TODO:userService에 findUserByNo 추가 후 email-> No로 변경
                UserDto fromUser = userService.findUserByEmail(fromUserEmail);
                UserDto toUser = userService.findUserByEmail(followRequest.getToUserEmail());
                followService.save(fromUser, toUser);
                final Response result = new Response("success", "팔로우를 성공하였습니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                final Response result = new Response("error", "유효하지 않은 토큰 값입니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
        } catch (NotFoundException e) {
            final Response result = new Response("error", e.getMessage(), null);
            response = new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            final Response result = new Response("error", "팔로우를 실패했습니다.", null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @DeleteMapping
    @ApiOperation(value = "팔로잉/팔로워 삭제(request객체의 email추후 삭제예정)",
            notes = "로그인한 사용자의 email과 팔로우를 해제한 사용자의 email을 전달받아 팔로잉/팔로워를 삭제한다.",
            response = Response.class)
    public Object deleteFollow(HttpServletRequest request, @Valid @RequestBody FollowRequest followRequest) {
        ResponseEntity response = null;
        try {
            Long fromUserNo = (long) jwtUtil.getNo(cookieUtil.getCookie(request, JwtUtil.ACCESS_TOKEN_NAME).getValue());
            if (fromUserNo != null) {
                followService.delete(fromUserNo,followRequest.getToUserNo());
                final Response result = new Response("success", "팔로잉/팔로워 삭제에 성공하였습니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                final Response result = new Response("error", "유효하지 않은 토큰 값입니다.", null);
                response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
        } catch (EntityNotFoundException e) {
            final Response result = new Response("error", e.getMessage(), null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            final Response result = new Response("error", "팔로잉/팔로워 삭제에 실패했습니다.", null);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}