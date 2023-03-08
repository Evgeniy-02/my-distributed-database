package ru.bmstu.labs.customdb.service;

import org.springframework.stereotype.Service;
import ru.bmstu.labs.customdb.dto.LaunchResponse;
import ru.bmstu.labs.customdb.dto.TerminateResponse;

@Service
public class DatabaseService {

    private boolean isActivate = true;

    public TerminateResponse terminate() {
        if (isActivate) {
            isActivate = false;
            return new TerminateResponse("SUCCESS");
        } else {
            return new TerminateResponse("FAILED: already terminated");
        }
    }

    public LaunchResponse launch() {
        if (!isActivate) {
            isActivate = true;
            return new LaunchResponse("SUCCESS");
        } else {
            return new LaunchResponse("FAILED: already launched");
        }
    }

    protected boolean isActivate() {
        return isActivate;
    }
}
