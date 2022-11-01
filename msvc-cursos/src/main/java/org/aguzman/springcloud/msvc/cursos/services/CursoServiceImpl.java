package org.aguzman.springcloud.msvc.cursos.services;

import org.aguzman.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.aguzman.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService {

    @Autowired
    private CursoRepository repository;

    @Autowired
    private UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> curso = repository.findById(cursoId);
        if (curso.isPresent()) {
            Usuario usuarioMsvc = client.detalle(usuario.getId());

            Curso cursoBD = curso.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());

            cursoBD.addCursoUsuario(cursoUsuario);
            repository.save(cursoBD);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> curso = repository.findById(cursoId);
        if (curso.isPresent()) {
            Usuario usuarioMsvc = client.crear(usuario);

            Curso cursoBD = curso.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());

            cursoBD.addCursoUsuario(cursoUsuario);
            repository.save(cursoBD);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> curso = repository.findById(cursoId);
        if (curso.isPresent()) {
            Usuario usuarioMsvc = client.detalle(usuario.getId());

            Curso cursoBD = curso.get();
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(usuarioMsvc.getId());

            cursoBD.removeCursoUsuario(cursoUsuario);
            repository.save(cursoBD);
            return Optional.of(usuarioMsvc);
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> cursoOptional = repository.findById(id);
        if (cursoOptional.isPresent()) {
            Curso curso = cursoOptional.get();
            if (!curso.getCursoUsuarios().isEmpty()) {
                List<Long> ids = curso.getCursoUsuarios().stream().map(
                        cu -> cu.getUsuarioId()).collect(Collectors.toList());
                List<Usuario> usuarios = client.obtenerAlumnosPorCurso(ids);
                curso.setUsuarios(usuarios);
            }
            return Optional.of(curso);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        repository.eliminarCursoUsuarioPorId(id);
    }
}
