package br.com.redemaissocial;

import br.com.redemaissocial.service.ServicoAfiliacao; 
import br.com.redemaissocial.domain.Candidato;
import br.com.redemaissocial.domain.Perfil; 
import br.com.redemaissocial.domain.Geografica;
import br.com.redemaissocial.domain.Fone;
import br.com.redemaissocial.domain.Virtual;
import java.util.Arrays; 

public class App {
    
    // hash de senha padrão para simplificar o teste de login
    private static final String SENHA_PADRAO_HASH = "SENHA_PADRAO_123"; 

    // --- MÉTODOS AUXILIARES DO FLUXO (5 PASSOS) ---

    private static Integer rodarFluxoAfiliacaoPF(ServicoAfiliacao servico, Candidato candidato) {
        
        System.out.println("\n--- iniciando fluxo de afiliação pf para " + candidato.getNome() + " ---");
        
        Candidato candidatoSalvo = null;

        // a. passo 1: verifica se o documento (cpf) já existe (duplicidade)
        if (servico.iniciarAfiliacao(candidato.getDocumento()) != null) {
            return null;
        }

        // b. passo 2: salva os dados de identificação (entidade e pessoafisica)
        System.out.println("[passo 2] salvando os dados de identificação...");
        candidatoSalvo = servico.processarIdentificacao(candidato);

        if (candidatoSalvo != null) {
            
            // c. passo 3: simula e processa a localização (geografica, fone, virtual)
            System.out.println("\n[passo 3] processando localização (geografica, fone e virtual)...");
            
            // simulação dos objetos de localização para persistência
            Geografica geo = new Geografica(); geo.setCandidato(candidatoSalvo); geo.setCep("01000000"); geo.setLogradouro("rua teste pf"); geo.setNumero("10"); 
            geo.setBairro("centro teste"); geo.setCidade("são paulo"); geo.setEstado("sp"); geo.setComplemento("ap. 101");
            Fone fone = new Fone(); fone.setCandidato(candidatoSalvo); fone.setTipo("celular"); fone.setDdi("55"); fone.setDdd("11"); fone.setNumeroCompleto("987654321");
            Virtual virtual = new Virtual(); virtual.setCandidato(candidatoSalvo); virtual.setTipo("website"); virtual.setContato("www.redemaissocial.com.br/pf-" + candidatoSalvo.getId());

            boolean localizacaoSalva = servico.processarLocalizacao(geo, fone, virtual);
            
            if (localizacaoSalva) {
            
                // d. passo 4: processa o perfil (descricao, habilidades e interesses)
                System.out.println("\n[passo 4] processando perfil...");
                Perfil novoPerfil = new Perfil(); novoPerfil.setCandidato(candidatoSalvo); novoPerfil.setDescricao("busca atuar em projetos de teste e validação.");
                novoPerfil.setHabilidades(Arrays.asList("java", "sql")); novoPerfil.setInteresses(Arrays.asList("testes", "arquitetura"));
                Perfil perfilSalvo = servico.processarPerfil(novoPerfil);
                
                if (perfilSalvo != null) {
                    // e. passo 5: registra a aceitação do termo de compromisso
                    System.out.println("\n[passo 5] processando termo de compromisso...");
                    boolean afilicaoConcluida = servico.processarTermo(candidatoSalvo);

                    if (afilicaoConcluida) {
                        System.out.println("sucesso: termo de compromisso registrado.");
                        System.out.println("afiliação concluída! candidato pode acessar o sistema.");
                        System.out.println("\n[sucesso completo] afiliação pf finalizada.");
                        return candidatoSalvo.getId(); // retorna o id para o teste de login
                    }
                }
            }
        }
        
        System.out.println("\n--- fluxo interrompido ---");
        return null; // retorna null se o fluxo falhar em qualquer passo
    }

    private static Integer rodarFluxoAfiliacaoPJ(ServicoAfiliacao servico, Candidato candidato) {
        
        System.out.println("\n--- iniciando fluxo de afiliação pj para " + candidato.getRazaoSocial() + " ---");
        
        Candidato candidatoSalvo = null;

        // a. passo 1: verifica se o documento (cnpj) já existe
        if (servico.iniciarAfiliacao(candidato.getDocumento()) != null) {
            return null;
        }

        // b. passo 2: salva os dados de identificação (entidade e pessoajuridica)
        System.out.println("[passo 2] salvando os dados de identificação...");
        candidatoSalvo = servico.processarIdentificacao(candidato);

        if (candidatoSalvo != null) {
            
            // c. passo 3: simula e processa a localização (geografica, fone, virtual)
            System.out.println("\n[passo 3] processando localização (geografica, fone e virtual)...");
            
            // simulação dos objetos de localização para persistência pj
            Geografica geo = new Geografica(); geo.setCandidato(candidatoSalvo); geo.setCep("01000000"); geo.setLogradouro("av. corporativa"); geo.setNumero("123"); geo.setBairro("industrial"); geo.setCidade("sp"); geo.setEstado("sp");
            Fone fone = new Fone(); fone.setCandidato(candidatoSalvo); fone.setTipo("comercial"); fone.setDdi("55"); fone.setDdd("11"); fone.setNumeroCompleto("22221111");
            Virtual virtual = new Virtual(); virtual.setCandidato(candidatoSalvo); virtual.setTipo("website"); virtual.setContato("www.empresa.com.br/id-" + candidatoSalvo.getId());

            boolean localizacaoSalva = servico.processarLocalizacao(geo, fone, virtual);

            if (localizacaoSalva) {
                
                // d. passo 4: processa o perfil (descrição, habilidades e interesses)
                System.out.println("\n[passo 4] processando perfil...");
                Perfil novoPerfil = new Perfil(); novoPerfil.setCandidato(candidatoSalvo); novoPerfil.setDescricao("atuamos no setor de tecnologia."); novoPerfil.setHabilidades(Arrays.asList("consultoria", "gestão")); novoPerfil.setInteresses(Arrays.asList("inovação", "sustentabilidade"));
                Perfil perfilSalvo = servico.processarPerfil(novoPerfil);
                
                if (perfilSalvo != null) {
                    
                    // e. passo 5: registra a aceitação do termo de compromisso
                    System.out.println("\n[passo 5] processando termo de compromisso...");
                    boolean afilicaoConcluida = servico.processarTermo(candidatoSalvo);

                    if (afilicaoConcluida) {
                        System.out.println("sucesso: termo de compromisso registrado.");
                        System.out.println("afiliação concluída! candidato pode acessar o sistema.");
                        System.out.println("\n[sucesso completo] afiliação pj finalizada.");
                        return candidatoSalvo.getId(); // retorna o id para o teste de login
                    }
                }
            }
        }
        
        System.out.println("\n--- fluxo interrompido ---");
        return null;
    }


    // --- main ---
    public static void main(String[] args) {
        
        // inicializa o serviço principal de afiliação
        ServicoAfiliacao servico = new ServicoAfiliacao();
        
        // ⚠️ dados de teste (mude estes 4 valores em cada execução para garantir unicidade)
        String documentoPF = "11111111115"; 
        String emailPF = "pf.teste.final.5@completo.com";
        String documentoPJ = "98989898983007"; 
        String emailPJ = "pj.teste.final.7@redesocial.com";
        
        // =======================================================
        // cenário 1: robustez - validação falha (teste de documento/email inválido)
        // =======================================================
        System.out.println("---------------------------------------------------");
        System.out.println("cenário 1: teste de validação (cpf/email inválido)");
        System.out.println("---------------------------------------------------");
        Candidato candidatoInvalido = new Candidato(); candidatoInvalido.setNome("falha"); candidatoInvalido.setDocumento("123"); candidatoInvalido.setEmail("email-invalido");
        rodarFluxoAfiliacaoPF(servico, candidatoInvalido);

        // =======================================================
        // cenário 2: fluxo principal completo (pf)
        // =======================================================
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 2: fluxo principal completo (pf)");
        System.out.println("---------------------------------------------------");
        Candidato candidatoPF = new Candidato(); 
        candidatoPF.setNome("candidato pf final"); 
        candidatoPF.setDocumento(documentoPF); 
        candidatoPF.setEmail(emailPF);
        Integer idCandidatoPF = rodarFluxoAfiliacaoPF(servico, candidatoPF);

        // =======================================================
        // cenário 3: fluxo alternativo (duplicidade pf)
        // =======================================================
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 3: fluxo alternativo (duplicidade pf)");
        System.out.println("---------------------------------------------------");
        // tenta cadastrar o mesmo pf novamente
        rodarFluxoAfiliacaoPF(servico, candidatoPF);
        
        // =======================================================
        // cenário 4: fluxo principal completo (pj)
        // =======================================================
        System.out.println("\n---------------------------------------------------");
        System.out.println("cenário 4: fluxo pj completo (com dados adicionais)");
        System.out.println("---------------------------------------------------");
        
        Candidato candidatoPJ = new Candidato();
        candidatoPJ.setNomeFantasia("redemais social pj teste"); 
        candidatoPJ.setDocumento(documentoPJ);
        candidatoPJ.setEmail(emailPJ);
        // atributos de pj (simulação de preenchimento dos campos extras)
        candidatoPJ.setRazaoSocial("rede mais social tecnologia e servicos ltda pj");
        candidatoPJ.setCnae("6204-0/00");
        candidatoPJ.setNaturezaJuridica("206-2");
        Integer idCandidatoPJ = rodarFluxoAfiliacaoPJ(servico, candidatoPJ);
        
        // =======================================================
        // cenário 5: uc003 - realizar login (pf e pj)
        // =======================================================
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