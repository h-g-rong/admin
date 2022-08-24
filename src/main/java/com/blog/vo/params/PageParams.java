package com.blog.vo.params;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageParams {
    private int page=1;
    private int size=4;
    private Long categoryId;
    private Long tagId;
    private String year;
    private String month;
    private Long authorId;

    public String getMonth(){
        if (this.month != null && this.month.length() == 1){
            return "0"+this.month;
        }
        return this.month;
    }

}
