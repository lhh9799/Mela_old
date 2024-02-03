package com.ssafy.api.shorts.controller;

import com.ssafy.api.file.service.FileService;
import com.ssafy.api.shorts.request.ShortsPostReq;
import com.ssafy.api.shorts.service.ShortsService;
import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.Shorts;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Api(value = "쇼츠 API", tags = {"Shorts"})
@RestController
@RequestMapping("/api/v1/shorts")
public class ShortsController {
    @Autowired
    FileService fileService;

    @Autowired
    ShortsService shortsService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "쇼츠 동영상 등록", notes = "쇼츠 동영상을 등록한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "서버 오류")
    })

    public ResponseEntity<? extends BaseResponseBody> uploadShorts (
            @ApiIgnore Authentication authentication,
            @RequestPart @ApiParam(value="shortsPostReq", required = true) ShortsPostReq shortsPostReq,
            @RequestPart(value = "file", required = true) MultipartFile multipartFile) {

        Shorts shorts = new Shorts();

        //TODO: 토큰 유효성 확인

        //1. 토큰으로부터 사용자 확인 후 VO에 설정
        SsafyUserDetails userDetails = (SsafyUserDetails)authentication.getDetails();
        shorts.setUserIdx(userDetails.getUser());

        //2. Amazon S3에 파일 저장
        int returnCode = shortsService.uploadShorts(shorts, multipartFile, shortsPostReq);

        if(returnCode == 200) {             //파일 정상 저장 (Response 200)
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
        } else if(returnCode == 415) {      //지원하지 않는 확장자 (
            return ResponseEntity.status(415).body(BaseResponseBody.of(415, "Fail (Unsupported Video Extension)"));
        } else {                            //파일 저장 실패 (Response 500)
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Fail"));
        }
    }

    @DeleteMapping(value = "/{shortsid}")
    @ApiOperation(value = "쇼츠 동영상 삭제", notes = "쇼츠 동영상을 삭제한다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 500, message = "삭제 실패"),
    })

    public ResponseEntity<? extends BaseResponseBody> deleteShorts (
            @ApiIgnore Authentication authentication,
            @PathVariable(name = "shortsid") Long shortsid) {

        //TODO: 토큰 유효성 확인

        //1. 클라이언트로부터 전달받은 shortsid로
        //FileRepository를 이용해
        //com.ssafy.db.entity 패키지 -> Shorts 클래스 -> File 타입의 shortsPathFileIdx 객체 가져오기
        com.ssafy.db.entity.File file = shortsService.getFileInstanceByShortsIdx(shortsid);

        //2. FileService클래스의 deleteFile 메소드를 이용해 파일 삭제 (Amazon S3에서의 삭제 및 file 테이블에서의 삭제)
        if(fileService.deleteFileByFileInstance(file)) {
            return ResponseEntity.status(200).body(BaseResponseBody.of(200, "Success"));
        } else {
            return ResponseEntity.status(500).body(BaseResponseBody.of(500, "Fail"));
        }
    }
}
