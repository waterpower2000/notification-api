package com.sysbean.campusive.app.dto;

import br.com.caelum.vraptor.Get;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationDefaulterDTO {
    private String phoneNumber;
    private String notificationId;
    private  String errorMessage;
}
