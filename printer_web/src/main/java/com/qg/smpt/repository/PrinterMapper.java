package com.qg.smpt.repository;

import com.qg.smpt.model.Printer;
import org.springframework.stereotype.Repository;


@Repository
public interface PrinterMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table printer
     *
     * @mbggenerated Sat Apr 08 11:09:21 CST 2017
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table printer
     *
     * @mbggenerated Sat Apr 08 11:09:21 CST 2017
     */
    int insert(Printer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table printer
     *
     * @mbggenerated Sat Apr 08 11:09:21 CST 2017
     */
    int insertSelective(Printer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table printer
     *
     * @mbggenerated Sat Apr 08 11:09:21 CST 2017
     */
    Printer selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table printer
     *
     * @mbggenerated Sat Apr 08 11:09:21 CST 2017
     */
    int updateByPrimaryKeySelective(Printer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table printer
     *
     * @mbggenerated Sat Apr 08 11:09:21 CST 2017
     */
    int updateByPrimaryKey(Printer record);
}