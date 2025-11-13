package br.com.redemaissocial;

import br.com.redemaissocial.service.ServicoAfiliacao; 
import br.com.redemaissocial.domain.Candidato;
import br.com.redemaissocial.domain.Perfil; 
import br.com.redemaissocial.domain.Geografica;
import br.com.redemaissocial.domain.Fone;
import br.com.redemaissocial.domain.Virtual;
import br.com.redemaissocial.domain.Formacao; 
import java.time.LocalDate;
import java.util.Arrays; 
import java.util.List;

public class App {
    
    // hash de senha padrão para simplificar o teste de login
    private static final String SENHA_PADRAO_HASH = "SENHA_PADRAO_123"; 

    // MÉTODOS AUXILIARES DO FLUXO (6 PASSOS)

    private static Integer rodarFluxoAfiliacaoPF(ServicoAfiliacao servico, Candidato candidato) {
        
        System.out.println("\n--- iniciando fluxo de afiliação pf para " + candidato.getNome() + " ---");
        
        Candidato candidatoSalvo = null;

        // passo 1: verifica se o documento (cpf) já existe (duplicidade)
        if (servico.iniciarAfiliacao(candidato.getDocumento()) != null) {
            System.err.println("erro: documento " + candidato.getDocumento() + " já cadastrado para " + candidato.getNome());
            System.out.println("[PASSO 1] DUPLICIDADE: Documento já existe na base.");
            return null;
        }

        // passo 2: salva os dados de identificação (entidade e pessoafisica)
        System.out.println("[passo 2] salvando os dados de identificação...");
        candidatoSalvo = servico.processarIdentificacao(candidato);

        if (candidatoSalvo == null) {
            System.err.println("falha no passo 2: identificação.");
            return null;
        }

        // passo 3: processa localização (geografica, fone e virtual)
        System.out.println("[passo 3] processando localização (geografica, fone e virtual)...");
        if (!servico.processarLocalizacao(candidatoSalvo)) {
            System.err.println("falha no passo 3: localização/contato.");
            return null;
        }
        
        // passo 4: processa formação acadêmica
        System.out.println("[passo 4] processando formações...");
        if (!servico.processarFormacao(candidatoSalvo)) {
            System.err.println("falha no passo 4: formação.");
            return null;
        }

        // passo 5: processa perfil (descrição, habilidades e interesses)
        Perfil perfil = candidato.getPerfil();
        System.out.println("[passo 5] processando perfil...");
        Integer perfilId = servico.processarPerfil(candidatoSalvo.getId(), perfil);
        
        if (perfilId == null) {
            System.err.println("falha no passo 5: perfil.");
            return null;
        }
        
        // passo 6: aceita termo de compromisso
        System.out.println("[passo 6] aceitando termo de compromisso...");
        if (!servico.processarTermo(candidatoSalvo)) {
            System.err.println("falha no passo 6: termo de compromisso.");
            return null;
        }

        System.out.println("--- fluxo pf concluído com sucesso ---");
        return candidatoSalvo.getId();
    }

    private static Integer rodarFluxoAfiliacaoPJ(ServicoAfiliacao servico, Candidato candidato) {
        
        System.out.println("\n--- iniciando fluxo de afiliação pj para " + candidato.getNome() + " ---");
        
        Candidato candidatoSalvo = null;

        // passo 1: verifica se o documento (cnpj) já existe (duplicidade)
        if (servico.iniciarAfiliacao(candidato.getDocumento()) != null) {
            System.err.println("erro: documento " + candidato.getDocumento() + " já cadastrado para " + candidato.getNome());
            System.out.println("[PASSO 1] DUPLICIDADE: Documento já existe na base.");
            return null;
        }

        // passo 2: salva os dados de identificação (entidade e pessoajuridica)
        System.out.println("[passo 2] salvando os dados de identificação...");
        candidatoSalvo = servico.processarIdentificacao(candidato);

        if (candidatoSalvo == null) {
            System.err.println("falha no passo 2: identificação.");
            return null;
        }

        // passo 3: processa localização (geografica, fone e virtual)
        System.out.println("[passo 3] processando localização (geografica, fone e virtual)...");
        if (!servico.processarLocalizacao(candidatoSalvo)) {
            System.err.println("falha no passo 3: localização/contato.");
            return null;
        }

        // passo 4: processa formação acadêmica/profissional (NOVO PASSO)
        System.out.println("[passo 4] processando formações...");
        if (!servico.processarFormacao(candidatoSalvo)) {
            System.err.println("falha no passo 4: formação.");
            return null;
        }
        
        // passo 5: processa perfil (descrição, habilidades e interesses)
        Perfil perfil = candidato.getPerfil();
        System.out.println("[passo 5] processando perfil...");
        Integer perfilId = servico.processarPerfil(candidatoSalvo.getId(), perfil);
        
        if (perfilId == null) {
            System.err.println("falha no passo 5: perfil.");
            return null;
        }
        
        // passo 6: aceita termo de compromisso
        System.out.println("[passo 6] aceitando termo de compromisso...");
        if (!servico.processarTermo(candidatoSalvo)) {
            System.err.println("falha no passo 6: termo de compromisso.");
            return null;
        }

        System.out.println("--- fluxo pj concluído com sucesso ---");
        return candidatoSalvo.getId();
    }


    public static void main(String[] args) {
        
        ServicoAfiliacao servico = new ServicoAfiliacao();

        // ---------------------------------------------------
        System.out.println("---------------------------------------------------");
        System.out.println("cenário 1: teste de validação (cpf/email inválido)");
        System.out.println("---------------------------------------------------");

        // iniciando fluxo de afiliação pf para falha 
        Candidato falhaPF = new Candidato();
        falhaPF.setNome("Candidato Falha");
        falhaPF.setDocumento("11111111111");
        falhaPF.setEmail("email_invalido.com"); 
        
        System.out.println("--- iniciando fluxo de afiliação pf para falha ---");
        System.out.println("[passo 2] salvando os dados de identificação...");
        
        if (servico.iniciarAfiliacao(falhaPF.getDocumento()) == null) {
            if (servico.processarIdentificacao(falhaPF) == null) {
                System.err.println("falha no passo 2: identificação.");
            }
        } else {
             System.out.println("[PASSO 1] DUPLICIDADE: Documento já existe na base.");
        }
        System.out.println("--- fluxo interrompido ---");


        
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 2: fluxo principal completo (pf)");
        System.out.println("---------------------------------------------------");
        
        // candidato PF 

        // dados de teste 
        String documentoPF = "11111111239"; 
        String emailPF = "pf.teste.final.23@completo.com"; 
        
        // constrói o objeto candidato pf
        Candidato candidatoPF = new Candidato();
        candidatoPF.setNome("Candidato PF Final");
        candidatoPF.setDocumento(documentoPF);
        candidatoPF.setEmail(emailPF);
        candidatoPF.setDataNascimento(LocalDate.of(1990, 5, 15));
        candidatoPF.setSexo("M");
        
        // localização (passo 3) ---
        Geografica geo = new Geografica();
        geo.setLogradouro("Rua das Oliveiras");
        geo.setNumero("400");
        geo.setCidade("São Paulo");
        geo.setEstado("SP");
        geo.setCep("01001-000");
        candidatoPF.setGeograficas(Arrays.asList(geo));

        Fone fone = new Fone();
        fone.setTipo("CELULAR");
        fone.setDdd("11");
        fone.setNumeroCompleto("998765432");
        candidatoPF.setFones(Arrays.asList(fone));

        Virtual virtual = new Virtual();
        virtual.setTipo("linkedin");
        virtual.setContato("linkedin.com/in/candidatopf");
        candidatoPF.setVirtuais(Arrays.asList(virtual));
        
        // formações (passo 4) 
        Formacao formacao1 = new Formacao();
        formacao1.setCurso("Engenharia de Software");
        formacao1.setTipo("Bacharelado");
        
        Formacao formacao2 = new Formacao();
        formacao2.setCurso("Desenvolvedor Full-Stack");
        formacao2.setTipo("Curso Livre");
        
        candidatoPF.setFormacoes(Arrays.asList(formacao1, formacao2));

        // perfil (passo 5)
        Perfil perfilPF = new Perfil();
        perfilPF.setDescricao("Desenvolvedor Java Pleno com foco em microserviços e experiência em Spring Boot.");
        perfilPF.setHabilidades(Arrays.asList("Java", "Spring Boot", "MySQL", "AWS"));
        perfilPF.setInteresses(Arrays.asList("Inteligência Artificial", "Liderança", "Open Source"));
        candidatoPF.setPerfil(perfilPF);
        
        // executa o fluxo PF 
        Integer idCandidatoPF = rodarFluxoAfiliacaoPF(servico, candidatoPF);

        
        // ---------------------------------------------------
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 3: fluxo alternativo (duplicidade pf)");
        System.out.println("---------------------------------------------------");
        
        // candidato PF (duplicado) 
        Candidato candidatoDuplicado = new Candidato();
        candidatoDuplicado.setNome("candidato pf final");
        candidatoDuplicado.setDocumento(documentoPF); // usa o mesmo documento do cenário 2
        candidatoDuplicado.setEmail("duplicado@completo.com");
        
        System.out.println("--- iniciando fluxo de afiliação pf para " + candidatoDuplicado.getNome() + " ---");
        // executa o passo 1 (duplicidade)
        servico.iniciarAfiliacao(candidatoDuplicado.getDocumento());
        
        
        // ---------------------------------------------------
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 4: fluxo pj completo (com dados adicionais)");
        System.out.println("---------------------------------------------------");

        // candidato PJ 

        // dados de teste 
        String documentoPJ = "98989898985004"; 
        String emailPJ = "pj.teste.final.23@redesocial.com"; 
        
        // constrói o objeto candidato pj
        Candidato candidatoPJ = new Candidato();
        candidatoPJ.setNome("Rede Mais Social Tecnologia e Serviços Ltda PJ");
        candidatoPJ.setDocumento(documentoPJ);
        candidatoPJ.setEmail(emailPJ);
        candidatoPJ.setRazaoSocial("REDE MAIS SOCIAL TECNOLOGIA E SERVICOS LTDA");
        candidatoPJ.setNomeFantasia("RedeMaisSocial Soluções");
        candidatoPJ.setCnae("6204-0/00");
        candidatoPJ.setNaturezaJuridica("206-2");
        
        // localização (passo 3) usa os mesmos dados para simplificar
        candidatoPJ.setGeograficas(Arrays.asList(geo)); 
        candidatoPJ.setFones(Arrays.asList(fone));
        candidatoPJ.setVirtuais(Arrays.asList(virtual));
        
        // formações (passo 4) 
        Formacao formacaoPJ = new Formacao();
        formacaoPJ.setCurso("Certificação ISO 9001");
        formacaoPJ.setTipo("Certificação Empresarial");
        
        candidatoPJ.setFormacoes(Arrays.asList(formacaoPJ));

        // perfil (passo 5) 
        Perfil perfilPJ = new Perfil();
        perfilPJ.setDescricao("Empresa líder em desenvolvimento de plataformas sociais para ONGs e projetos de impacto.");
        perfilPJ.setHabilidades(Arrays.asList("UX/UI Design", "Marketing Digital", "Java EE"));
        perfilPJ.setInteresses(Arrays.asList("Sustentabilidade", "Inovação Social", "FinTech"));
        candidatoPJ.setPerfil(perfilPJ);
        
        // executa o fluxo PJ 
        Integer idCandidatoPJ = rodarFluxoAfiliacaoPJ(servico, candidatoPJ);
        
      
        // cenário de realizar login (pf e pj)
        
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 5: uc003 - login com sucesso");
        System.out.println("---------------------------------------------------");
        
        if (idCandidatoPF != null) {
            System.out.println("log: tentativa de login para: " + emailPF);
            // testa o login usando o email e o hash de senha
            System.out.println("teste login pf: " + servico.realizarLogin(emailPF, SENHA_PADRAO_HASH));
        } else {
             System.out.println("impossível testar login pf: o cadastro no cenário 2 falhou.");
        }
        
        if (idCandidatoPJ != null) {
            System.out.println("log: tentativa de login para: " + emailPJ);
            // testa o login usando o email e o hash de senha
            System.out.println("teste login pj: " + servico.realizarLogin(emailPJ, SENHA_PADRAO_HASH));
        } else {
             System.out.println("impossível testar login pj: o cadastro no cenário 4 falhou.");
        }

        System.out.println("\n---------------------------------------------------");
    }
}