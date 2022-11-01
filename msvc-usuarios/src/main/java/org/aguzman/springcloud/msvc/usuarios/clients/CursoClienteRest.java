package org.aguzman.springcloud.msvc.usuarios.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient(name = "msvc-cursos", url = "localhost:8002")
public interface CursoClienteRest {

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    void eliminarCursoUsuarioPorId(Long id);

}
