package com.bdis.bdis_website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bdis.bdis_website.entity.ResumeSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ResumeSubmissionMapper extends BaseMapper<ResumeSubmission> {
    @Update("UPDATE resume_submission SET mail_status=1, mail_recipient=#{recipient}, mail_sent_at=NULL, mail_last_error=NULL "
            + "WHERE id=#{id} AND (mail_status IS NULL OR mail_status<>1)")
    int claimMail(@Param("id") Long id, @Param("recipient") String recipient);
}
